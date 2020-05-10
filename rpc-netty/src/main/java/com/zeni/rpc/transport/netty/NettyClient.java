package com.zeni.rpc.transport.netty;

import com.zeni.rpc.transport.InFlightRequests;
import com.zeni.rpc.transport.Transport;
import com.zeni.rpc.transport.TransportClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class NettyClient implements TransportClient {

    private EventLoopGroup eventLoopGroup;
    private Bootstrap bootstrap;
    private InFlightRequests inFlightRequests;
    private List<Channel> channels = new ArrayList<>();

    public NettyClient(){
        this.inFlightRequests = new InFlightRequests();
    }

    private Bootstrap newBootstrap(ChannelHandler channelHandler, EventLoopGroup eventLoopGroup){
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)
                .group(eventLoopGroup)
                .handler(channelHandler)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        return bootstrap;
    }



    @Override
    public Transport createTransport(SocketAddress address, long connectTimeout) throws InterruptedException, TimeoutException {
        return new NettyTransport(createChannel(address, connectTimeout), inFlightRequests);
    }

    private Channel createChannel(SocketAddress address, long connectTimeout) throws InterruptedException, TimeoutException {
        if (address == null ){
            throw  new IllegalArgumentException("address must not be null!");
        }
        if ( eventLoopGroup == null ){
            eventLoopGroup = newNioEventGroup();
        }

        if (bootstrap == null){
            ChannelHandler channelHandler = newChannelHandlerPipeline();
            bootstrap = newBootstrap(channelHandler, eventLoopGroup);
        }

        ChannelFuture channelFuture = bootstrap.connect(address);
        if (!channelFuture.await(connectTimeout)){
            throw new TimeoutException();
        }

        Channel channel = channelFuture.channel();
        if (channel == null || !channel.isActive()){
            throw  new IllegalStateException();
        }

        channels.add(channel);

        return channel;
    }


    private EventLoopGroup newNioEventGroup(){
        if (Epoll.isAvailable()){
            return new EpollEventLoopGroup();
        }else {
            return new NioEventLoopGroup();
        }
    }

    private ChannelHandler newChannelHandlerPipeline(){
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                channel.pipeline()
                        .addLast(new ResponseDecoder())
                        .addLast(new RequestEncoder())
                        .addLast(new ResponseInvocation(inFlightRequests));
            }
        };
    }

    @Override
    public void close() throws IOException {
        for (Channel channel : channels) {
            if (channel != null ){
                channel.close();
            }
        }
        if (eventLoopGroup != null){
            eventLoopGroup.shutdownGracefully();
        }
        inFlightRequests.close();

    }
}
