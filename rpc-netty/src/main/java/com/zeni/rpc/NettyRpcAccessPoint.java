package com.zeni.rpc;

import com.zeni.rpc.client.StubFactory;
import com.zeni.rpc.server.ServiceProviderRegistry;
import com.zeni.rpc.spi.ServiceSupport;
import com.zeni.rpc.transport.*;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class NettyRpcAccessPoint implements RpcAccessPoint {

    private final String host = "localhost";
    private final int port = 9999;
    private final URI uri = URI.create("rpc://" + host + ":" + port);

    private TransportServer server;
    private final Map<URI, Transport> clientMap = new HashMap<>();

    private TransportClient client = ServiceSupport.load(TransportClient.class);
    private final StubFactory stubFactory = ServiceSupport.load(StubFactory.class);
    private final ServiceProviderRegistry serviceProviderRegistry = ServiceSupport.load(ServiceProviderRegistry.class);

    @Override
    public <T> T getRemoteService(URI uri, Class<T> serviceClass) {
        Transport transport = clientMap.computeIfAbsent(uri, this::createTransport);
        return stubFactory.createStub(transport, serviceClass);
    }

    private Transport createTransport(URI uri) {
        try {
            return client.createTransport(new InetSocketAddress(uri.getHost(), uri.getPort()), 30000L);
        } catch (InterruptedException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> URI addServiceProvider(T service, Class<T> serviceClass) {
        serviceProviderRegistry.addServiceProvider(serviceClass, service);
        return uri;
    }

    @Override
    public Closeable startServer() throws Exception {
        if (server == null) {
            server = ServiceSupport.load(TransportServer.class);
            server.start(RequestHandlerRegistry.getInstance(), port);
        }

        return ()->{
            if (server != null){
                server.stop();
            }
        };
    }

    @Override
    public void close() throws IOException {
        if (server != null){
            server.stop();
        }
        client.close();
    }
}
