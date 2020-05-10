package com.zeni.rpc.transport.netty;

import com.zeni.rpc.transport.RequestHandlerRegistry;
import com.zeni.rpc.transport.TransportServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServer implements TransportServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private int port;
    private RequestHandlerRegistry requestHandlerRegistry;
    private EventLoopGroup  acceptEventGroup;
    private EventLoopGroup ioEventGroup;
    private Channel channel;

    @Override
    public void start(RequestHandlerRegistry requestHandlerRegistry, int port) throws Exception {
        this.port = port;
        this.requestHandlerRegistry =requestHandlerRegistry;
        acceptEventGroup = newEventLoopGroup();
        ioEventGroup = newEventLoopGroup();

        ChannelHandler channelHandler = newChannelHandlerPipeline();
        ServerBootstrap serverBootstrap = newBootstrap(acceptEventGroup, ioEventGroup, channelHandler);

        channel = doBind(serverBootstrap);

    }

    private EventLoopGroup newEventLoopGroup(){
        if (Epoll.isAvailable()){
            return new EpollEventLoopGroup();
        }else {
            return new NioEventLoopGroup();
        }
    }

    private ChannelHandler newChannelHandlerPipeline(){
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline()
                        .addLast(new RequestDecoder())
                        .addLast(new ResponseEncoder())
                        .addLast(new RequestInvocation(requestHandlerRegistry));

            }
        };
    }

    private ServerBootstrap newBootstrap(EventLoopGroup acceptEventGroup, EventLoopGroup ioEventGroup, ChannelHandler channelHandler){
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .group(acceptEventGroup, ioEventGroup)
                .childHandler(channelHandler)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        return serverBootstrap;
    }


    private Channel doBind(ServerBootstrap serverBootstrap) throws InterruptedException {
        return serverBootstrap.bind(port).sync().channel();
    }

    @Override
    public void stop() {
        if (acceptEventGroup != null){
            acceptEventGroup.shutdownGracefully();
        }
        if (ioEventGroup != null){
            ioEventGroup.shutdownGracefully();
        }
        if (channel != null){
            channel.close();
        }
    }
}
