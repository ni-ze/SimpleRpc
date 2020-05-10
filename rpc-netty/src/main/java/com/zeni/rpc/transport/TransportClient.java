package com.zeni.rpc.transport;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.TimeoutException;

public interface TransportClient extends Closeable {

    Transport createTransport(SocketAddress address, long connectTimeout) throws InterruptedException, TimeoutException;

    @Override
    void close() throws IOException;
}
