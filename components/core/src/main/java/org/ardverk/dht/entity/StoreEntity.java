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

package org.ardverk.dht.entity;

import org.ardverk.dht.message.Content;
import org.ardverk.dht.message.MessageType;
import org.ardverk.dht.message.StoreResponse;
import org.ardverk.dht.routing.Contact;
import org.ardverk.dht.storage.Key;

/**
 * The result of a {@link MessageType#STORE} operation.
 */
public interface StoreEntity extends Entity {
    
    /**
     * Returns all {@link Contact}s along the store path.
     */
    public Contact[] getContacts();
    
    /**
     * Returns the {@link Key} that was stored.
     */
    public Key getResourceId();
    
    /**
     * Returns the {@link Content} that was stored.
     */
    public Content getContent();
    
    /**
     * Returns all {@link StoreResponse}s.
     * 
     * <p>NOTE: {@link MessageType#STORE} requests are sent out in the order 
     * as defined in {@link #getContacts()} but the {@link StoreResponse}s 
     * are sorted in the order as arrived which depends heavily on things
     * such as network latency and system load on both ends of the system.
     */
    public StoreResponse[] getStoreResponses();
    
    /**
     * Returns the {@link Contact}s where we attempted to store
     * a value and received responses from.
     * 
     * @see #getStoreResponses()
     */
    public Contact[] getStoreContacts();
    
    /**
     * Returns {@code true} if all {@link StoreResponse}s indicate
     * that they were successful.
     * 
     * @see #getStoreResponses()
     */
    public boolean isSuccess();
}