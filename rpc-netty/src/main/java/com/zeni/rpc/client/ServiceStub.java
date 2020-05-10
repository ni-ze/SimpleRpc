package com.zeni.rpc.client;

import com.zeni.rpc.transport.Transport;

public interface ServiceStub {
    void setTransport(Transport transport);
}
