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

package org.ardverk.dht.io;

import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.ardverk.dht.ArdverkUtils;
import org.ardverk.dht.KUID;
import org.ardverk.dht.concurrent.DHTFuture;
import org.ardverk.dht.config.DefaultLookupConfig;
import org.ardverk.dht.config.LookupConfig;
import org.ardverk.dht.easy.EasyDHT;
import org.ardverk.dht.entity.NodeEntity;
import org.ardverk.dht.routing.Contact;
import org.ardverk.dht.utils.XorComparator;
import org.ardverk.io.IoUtils;
import org.junit.Test;


public class NodeResponseHandlerTest {
    
    @Test
    public void lookup() throws Exception {
        List<EasyDHT> dhts = ArdverkUtils.createDHTs(256, 2000);
        try {
            ArdverkUtils.bootstrap(dhts);
            
            KUID lookupId = KUID.createRandom(20);
            
            // Sort the DHTs by their XOR distance to the given lookupId.
            TreeSet<KUID> expected = new TreeSet<KUID>(
                    new XorComparator(lookupId));
            for (EasyDHT dht : dhts) {
                expected.add(dht.getLocalhost().getId());
            }
            
            EasyDHT first = dhts.get(0);
            
            LookupConfig config = new DefaultLookupConfig();
            config.setLookupTimeout(20L, TimeUnit.SECONDS);
            
            DHTFuture<NodeEntity> future 
                = first.lookup(lookupId, config);
            NodeEntity entity = future.get();
            TestCase.assertEquals(lookupId, entity.getId());
            
            Contact[] contacts = entity.getContacts();
            Contact[] closest = entity.getClosest();
            
            // The Contacts in the response should be in the same order
            // as our DHT instances!
            int k = first.getRouteTable().getK();
            for (int i = 0; i < k && i < contacts.length; i++) {
                KUID contactId = contacts[i].getId();
                KUID closestId = closest[i].getId();
                
                KUID expectedId = expected.pollFirst();
                
                TestCase.assertEquals(expectedId, contactId);
                TestCase.assertEquals(expectedId, closestId);
                
                TestCase.assertSame(closest[i], contacts[i]);
            }
            
        } finally {
            IoUtils.closeAll(dhts);
        }
    }
}