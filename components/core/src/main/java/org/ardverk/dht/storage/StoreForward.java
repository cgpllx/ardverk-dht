/*
 * Copyright 2009-2011 Roger Kapsi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ardverk.dht.storage;

import org.ardverk.collection.CollectionUtils;
import org.ardverk.dht.KUID;
import org.ardverk.dht.concurrent.DHTFuture;
import org.ardverk.dht.config.StoreConfig;
import org.ardverk.dht.entity.StoreEntity;
import org.ardverk.dht.message.Value;
import org.ardverk.dht.routing.Contact;
import org.ardverk.dht.routing.RouteTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class implements the logic that is used to determinate weather or
 * not a value should be store-forwarded.
 */
public class StoreForward {

    private static final Logger LOG 
        = LoggerFactory.getLogger(StoreForward.class);
    
    private final RouteTable routeTable;
    
    private final Database database;
    
    private volatile Callback callback;
    
    public StoreForward(RouteTable routeTable, Database database) {
        this.routeTable = routeTable;
        this.database = database;
    }
    
    public void bind(Callback callback) {
        this.callback = callback;
    }
    
    public void unbind() {
        this.callback = null;
    }
    
    public boolean isBound() {
        return callback != null;
    }
    
    public void handleRequest(Contact contact) {
        handleContact(contact);
    }
    
    public void handleResponse(Contact contact) {
        handleContact(contact);
    }
    
    public void handleLateResponse(Contact contact) {
        handleContact(contact);
    }
    
    private void handleContact(Contact contact) {
        DatabaseConfig config = database.getDatabaseConfig();
        if (!config.isStoreForward()) {
            return;
        }
        
        Callback callback = this.callback;
        if (callback == null) {
            return;
        }
        
        if (contact.isInvisible()) {
            return;
        }
        
        KUID contactId = contact.getId();
        Contact[] contacts = routeTable.select(contactId);
        
        if (!isResponsible(contact, contacts)) {
            return;
        }
        
        StoreConfig storeConfig = config.getStoreConfig();
        
        Contact last = CollectionUtils.last(contacts);    
        Iterable<Key> keys 
            = database.values(contactId, last.getId());
        
        for (Key key : keys) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(routeTable.getLocalhost().getId() 
                        + " foward " + key
                        + " to " + contact.getId());
            }
            
            Value value = database.get(key);
            if (value != null) {
                callback.store(contact, key, value, storeConfig);
            }
        }
    }
    
    /**
     * Returns {@code true} if we're responsible for store-forwarding
     * a value to the given {@link Contact}.
     */
    private boolean isResponsible(Contact contact, Contact[] contacts) {
        Contact localhost = routeTable.getLocalhost();
        
        if (0 < contacts.length && !contact.equals(localhost)) {
            Contact first = CollectionUtils.first(contacts);
            
            // The contact isn't in our Route Table yet.
            if (first.equals(localhost)) {
                return true;
            }
            
            // The contact is in our Route Table, we're the second
            // closest to it and its instance ID has changed.
            if (1 < contacts.length && first.equals(contact)) {
                Contact second = CollectionUtils.nth(contacts, 1);
                if (second.equals(localhost) 
                        && isNewOrHasChanged(contact, first)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Returns {@code true} if the given {@link Contact} is either new
     * or if has changed (i.e. its instance ID has changed).
     */
    private static boolean isNewOrHasChanged(Contact contact, Contact existing) {
        if (existing != null) {
            return contact.getInstanceId() != existing.getInstanceId();
        }
        return true;
    }
    
    /**
     * The {@link Callback} is being called by the 
     * {@link StoreForward} service.
     * 
     * @see StoreForward#bind(Callback)
     */
    public static interface Callback {
        
        /**
         * Called by the {@link StoreForward} service for each {@link Value}
         * that needs to be sent to the given {@link Contact}.
         */
        public DHTFuture<StoreEntity> store(Contact dst, 
                Key key, Value value, StoreConfig config);
    }
}