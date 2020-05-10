package com.zeni.rpc.transport.netty;

import com.zeni.rpc.transport.Command.Command;
import com.zeni.rpc.transport.RequestHandler;
import com.zeni.rpc.transport.RequestHandlerRegistry;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestInvocation extends SimpleChannelInboundHandler<Command> {
    private static final Logger logger = LoggerFactory.getLogger(RequestInvocation.class);
    private RequestHandlerRegistry requestHandlerRegistry;

    public RequestInvocation(RequestHandlerRegistry requestHandlerRegistry) {
        this.requestHandlerRegistry = requestHandlerRegistry;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command msg) throws Exception {
        RequestHandler handler = requestHandlerRegistry.get(msg.getHeader().getType());

        if (handler != null) {
            Command response = handler.handler(msg);

            if (response != null) {
                ctx.channel().writeAndFlush(response).addListener((ChannelFutureListener) channelFuture -> {
                    if (!channelFuture.isSuccess()){
                        logger.warn("Write response failed!",channelFuture.cause());
                        channelFuture.channel().close();
                    }
                });
            } else {
                logger.warn("response is null!");
            }

        } else {
            throw new Exception(String.format("No handler for request with type: %d!", msg.getHeader().getType()));
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("Exception", cause);
        super.exceptionCaught(ctx, cause);

        if (ctx.channel().isActive()){
            ctx.close();
        }
    }
}
