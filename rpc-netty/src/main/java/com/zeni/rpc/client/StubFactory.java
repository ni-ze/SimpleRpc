package com.zeni.rpc.client;

import com.zeni.rpc.transport.Transport;

public interface StubFactory {
    <T> T createStub(Transport transport, Class<T> serviceClass);
}
