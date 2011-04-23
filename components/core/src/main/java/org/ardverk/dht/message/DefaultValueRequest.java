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

package org.ardverk.dht.message;

import java.net.SocketAddress;

import org.ardverk.dht.KUID;
import org.ardverk.dht.routing.Contact;
import org.ardverk.dht.storage.ResourceId;


public class DefaultValueRequest extends AbstractLookupRequest 
        implements ValueRequest {
    
    private final ResourceId recource;
    
    public DefaultValueRequest(MessageId messageId, Contact contact, 
            SocketAddress address, ResourceId recource) {
        super(messageId, contact, address);
        this.recource = recource;
    }
    
    @Override
    public KUID getId() {
        return recource.getId();
    }

    @Override
    public ResourceId getResource() {
        return recource;
    }
}