package com.zeni.rpc.client;

import java.util.concurrent.atomic.AtomicInteger;

public class RequestIdSupport {
    private static final  AtomicInteger nextRequestId = new AtomicInteger(0);

    public static int next(){
        return nextRequestId.getAndIncrement();
    }
}
