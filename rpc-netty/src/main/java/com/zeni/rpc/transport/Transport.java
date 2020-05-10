package com.zeni.rpc.transport;

import com.zeni.rpc.transport.Command.Command;
import java.util.concurrent.CompletableFuture;

public interface Transport {
    CompletableFuture<Command> send(Command request);
}
