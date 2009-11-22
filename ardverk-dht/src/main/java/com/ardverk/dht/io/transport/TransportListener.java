/**
 * 
 */
package com.ardverk.dht.io.transport;

import java.io.IOException;
import java.net.SocketAddress;

public interface TransportListener {
    public void received(SocketAddress src, byte[] message, 
            int offet, int length) throws IOException;
}