package com.zeni.rpc.transport;

import com.zeni.rpc.transport.Command.Command;
import java.util.concurrent.CompletableFuture;

public class ResponseFuture {
    private final int requestId;
    private final CompletableFuture<Command> completableFuture;
    private final long timestamp;

    public ResponseFuture(int requestId, CompletableFuture<Command> completableFuture){
        this.requestId = requestId;
        this.completableFuture = completableFuture;
        this.timestamp = System.nanoTime();
    }


    public int getRequestId(){
        return this.requestId;
    }

    public CompletableFuture<Command> getFuture(){
        return this.completableFuture;
    }

    long getTimestamp(){
        return this.timestamp;
    }
}
