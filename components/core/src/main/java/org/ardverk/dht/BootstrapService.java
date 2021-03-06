/*
 * Copyright 2009-2012 Roger Kapsi
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

package org.ardverk.dht;

import java.net.InetAddress;
import java.net.SocketAddress;

import org.ardverk.dht.concurrent.DHTFuture;
import org.ardverk.dht.config.BootstrapConfig;
import org.ardverk.dht.entity.BootstrapEntity;
import org.ardverk.dht.routing.Contact;


/**
 * The {@link BootstrapService} provides an interface to bootstrap a {@link DHT}.
 */
interface BootstrapService {

  /**
   * Bootstraps the DHT from the given hostname and port.
   */
  public DHTFuture<BootstrapEntity> bootstrap(
      String host, int port, BootstrapConfig config);
  
  /**
   * Bootstraps the DHT from the given {@link InetAddress} and port.
   */
  public DHTFuture<BootstrapEntity> bootstrap(
      InetAddress address, int port, BootstrapConfig config);
  
  /**
   * Bootstraps the DHT from the given {@link SocketAddress}.
   */
  public DHTFuture<BootstrapEntity> bootstrap(
      SocketAddress address, BootstrapConfig config);
  
  /**
   * Bootstraps the DHT from the given {@link Contact}.
   */
  public DHTFuture<BootstrapEntity> bootstrap(
      Contact contact, BootstrapConfig config);
}