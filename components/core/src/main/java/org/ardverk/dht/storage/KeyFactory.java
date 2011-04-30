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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public abstract class KeyFactory {
    
    private static final Map<String, KeyFactory> FACTORIES = createKeyFactoryMap();
    
    public static Key parseKey(String uri) {
        return parseKey(URI.create(uri));
    }
    
    public static Key parseKey(URI uri) {
        
        String scheme = uri.getScheme();
        KeyFactory factory = FACTORIES.get(scheme);
        if (factory != null) {
            return factory.valueOf(uri);
        }
        
        throw new IllegalArgumentException(uri.toString());
    }

    /**
     * 
     */
    public abstract String getScheme();
    
    /**
     * 
     */
    public abstract Key valueOf(URI uri);
    
    @Override
    public String toString() {
        return getScheme();
    }
    
    private static Map<String, KeyFactory> createKeyFactoryMap() {
        Map<String, KeyFactory> map 
            = new HashMap<String, KeyFactory>();
        
        ServiceLoader<KeyFactory> factories 
            = ServiceLoader.load(KeyFactory.class);
        
        for (KeyFactory factory : factories) {
            String scheme = factory.getScheme();
            map.put(scheme, factory);
        }
        
        return map;
    }
}
