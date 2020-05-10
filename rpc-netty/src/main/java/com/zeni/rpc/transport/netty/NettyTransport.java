package com.zeni.rpc.transport.netty;

import com.zeni.rpc.transport.Command.Command;
import com.zeni.rpc.transport.InFlightRequests;
import com.zeni.rpc.transport.ResponseFuture;
import com.zeni.rpc.transport.Transport;
import io.netty.channel.Channel;

import java.util.concurrent.CompletableFuture;

public class NettyTransport implements Transport {

    private final Channel channel;
    private final InFlightRequests inFlightRequests;

    public NettyTransport(Channel channel, InFlightRequests inFlightRequests) {
        this.channel = channel;
        this.inFlightRequests = inFlightRequests;
    }

    @Override
    public CompletableFuture<Command> send(Command request) {
        CompletableFuture<Command> completableFuture = new CompletableFuture<>();


        try{
            inFlightRequests.put(new ResponseFuture(request.getHeader().getRequestId(),completableFuture));

            channel.writeAndFlush(request).addListener((channelFuture) -> {
                if (!channelFuture.isSuccess()){
                    completableFuture.completeExceptionally(channelFuture.cause());
                    channel.close();
                }
            });
        }catch (Exception e){
            inFlightRequests.remove(request.getHeader().getRequestId());
            completableFuture.completeExceptionally(e);
        }

        return completableFuture;
    }
}
