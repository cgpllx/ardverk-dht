package com.ardverk.dht;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.ardverk.net.NetworkUtils;

import com.ardverk.dht.codec.DefaultMessageCodec;
import com.ardverk.dht.codec.MessageCodec;
import com.ardverk.dht.concurrent.ArdverkFuture;
import com.ardverk.dht.config.BootstrapConfig;
import com.ardverk.dht.config.DefaultBootstrapConfig;
import com.ardverk.dht.config.DefaultRefreshConfig;
import com.ardverk.dht.entity.BootstrapEntity;
import com.ardverk.dht.entity.RefreshEntity;
import com.ardverk.dht.io.transport.DatagramTransport;
import com.ardverk.dht.io.transport.Transport;
import com.ardverk.dht.message.DefaultMessageFactory;
import com.ardverk.dht.message.MessageFactory;
import com.ardverk.dht.routing.Contact;
import com.ardverk.dht.routing.DefaultRouteTable;
import com.ardverk.dht.routing.RouteTable;
import com.ardverk.dht.storage.Database;
import com.ardverk.dht.storage.DefaultDatabase;

class ArdverkUtils {

    private static final String SECRET_KEY = "90fb237cbec71523ba9d883a8ec6ae9f";
    private static final String INIT_VECTOR = "6fd7bda068bf2425980e5c9b1c9e2097";
    
    private static final int ID_SIZE = 20;
    
    private ArdverkUtils() {}
    
    public static ArdverkDHT createDHT(int port) throws IOException {
        return createDHT(new InetSocketAddress("localhost", port));
    }
    
    public static ArdverkDHT createDHT(SocketAddress address) throws IOException {
        Contact localhost = Contact.localhost(
                KUID.createRandom(ID_SIZE), address);
        
        MessageCodec codec = new DefaultMessageCodec(SECRET_KEY, INIT_VECTOR);
        
        MessageFactory messageFactory = new DefaultMessageFactory(
                ID_SIZE, localhost);
        
        Database database = new DefaultDatabase();
        RouteTable routeTable = new DefaultRouteTable(localhost);
        
        ArdverkDHT dht = new ArdverkDHT(codec, messageFactory, routeTable, database);
        
        Transport transport = new DatagramTransport(
                NetworkUtils.getPort(address));
        dht.getMessageDispatcher().bind(transport);
        
        return dht;
    }
    
    public static List<ArdverkDHT> createDHTs(int count, int port) throws IOException {
        List<ArdverkDHT> dhts = new ArrayList<ArdverkDHT>(count);
        for (int i = 0; i < count; i++) {
            dhts.add(createDHT(port + i));
        }
        
        return dhts;
    }
    
    public static List<ArdverkFuture<BootstrapEntity>> bootstrap(List<? extends DHT> dhts) 
            throws InterruptedException, ExecutionException {
        
        if (dhts.size() <= 1) {
            throw new IllegalArgumentException();
        }
        
        // Bootstrap everyone from the first DHT
        DHT first = dhts.get(0);
        List<ArdverkFuture<BootstrapEntity>> futures1 
            = bootstrap(first.getLocalhost(), dhts, 1, dhts.size()-1);
        
        // The RouteTable is all messed up! Clear it and bootstrap
        // the first DHT from the others.
        ((DefaultRouteTable)first.getRouteTable()).clear();
        List<ArdverkFuture<BootstrapEntity>> futures2 
            = bootstrap(dhts.get(1).getLocalhost(), dhts, 0, 1);
        
        futures2.addAll(futures1);
        return futures2;
    }
    
    public static List<ArdverkFuture<BootstrapEntity>> bootstrap(Contact from, 
            List<? extends DHT> dhts, int offset, int length) 
                throws InterruptedException, ExecutionException {
        
        List<ArdverkFuture<BootstrapEntity>> futures 
            = new ArrayList<ArdverkFuture<BootstrapEntity>>();
        
        for (int i = 0; i < length; i++) {
            BootstrapConfig config = new DefaultBootstrapConfig();
            config.setQueueKey(QueueKey.BACKEND);
            
            ArdverkFuture<BootstrapEntity> future 
                = dhts.get(offset+i).bootstrap(from, config);
            futures.add(future);
            future.get();
        }
        
        return futures;
    }
    
    public static List<ArdverkFuture<RefreshEntity>> refresh(List<? extends DHT> dhts) 
            throws InterruptedException, ExecutionException {
        return refresh(dhts, 0, dhts.size());
    }
    
    public static List<ArdverkFuture<RefreshEntity>> refresh(List<? extends DHT> dhts, int offset, int length) 
            throws InterruptedException, ExecutionException {
        
        List<ArdverkFuture<RefreshEntity>> futures 
            = new ArrayList<ArdverkFuture<RefreshEntity>>();
        
        for (int i = 0; i < length; i++) {
            DefaultRefreshConfig config = new DefaultRefreshConfig();
            config.setQueueKey(QueueKey.BACKEND);
            
            //config.setBucketTimeout(-1L, TimeUnit.MILLISECONDS);
            config.setBucketTimeout(1L, TimeUnit.MINUTES);
            
            ArdverkFuture<RefreshEntity> future 
                = dhts.get(offset + i).refresh(config);
            futures.add(future);
            future.get();
        }
        return futures;
    }
}