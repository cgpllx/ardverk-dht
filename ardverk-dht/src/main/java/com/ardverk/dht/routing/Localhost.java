/*
 * Copyright 2009-2010 Roger Kapsi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ardverk.dht.routing;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

import org.ardverk.net.NetworkUtils;

import com.ardverk.dht.KUID;

public class Localhost extends AbstractContact {
    
    private static final long serialVersionUID = 1919885060478043754L;

    private final long creationTime = System.currentTimeMillis();
    
    private volatile int instanceId = 0;
    
    private volatile SocketAddress socketAddress;
    
    private volatile SocketAddress contactAddress;
    
    public Localhost(KUID contactId, int port) {
        this(contactId, new InetSocketAddress(port));
    }
    
    public Localhost(KUID contactId, String host, int port) {
        this(contactId, NetworkUtils.createUnresolved(host, port));
    }
    
    public Localhost(KUID contactId, InetAddress address, int port) {
        this(contactId, NetworkUtils.createResolved(address, port));
    }
    
    public Localhost(KUID contactId, SocketAddress address) {
        super(contactId);
        
        this.socketAddress = address;
        this.contactAddress = address;
    }
    
    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public long getTimeStamp() {
        return System.currentTimeMillis();
    }
    
    @Override
    public Type getType() {
        return Type.AUTHORITATIVE;
    }

    @Override
    public int getInstanceId() {
        return instanceId;
    }
    
    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    @Override
    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    @Override
    public SocketAddress getContactAddress() {
        return contactAddress;
    }
    
    public void setContactAddress(SocketAddress contactAddress) {
        this.contactAddress = contactAddress;
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return getContactAddress();
    }

    @Override
    public long getRoundTripTime(TimeUnit unit) {
        return -1L;
    }
    
    @Override
    public void setRoundTripTime(long rtt, TimeUnit unit) {
        // Do nothing, a localhost cannot have a RTT
    }

    @Override
    public Contact merge(Contact other) {
        throw new UnsupportedOperationException();
    }
}