package com.zeni.rpc.transport.netty;

import com.zeni.rpc.transport.Command.Command;
import com.zeni.rpc.transport.InFlightRequests;
import com.zeni.rpc.transport.ResponseFuture;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseInvocation extends SimpleChannelInboundHandler<Command> {
    private static final Logger logger = LoggerFactory.getLogger(ResponseInvocation.class);
    private InFlightRequests inFlightRequests;

    public ResponseInvocation(InFlightRequests inFlightRequests){
        this.inFlightRequests = inFlightRequests;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command response) throws Exception {
        ResponseFuture future = inFlightRequests.remove(response.getHeader().getRequestId());
        if (future != null){
            future.getFuture().complete(response);
        }else {
            logger.warn("Drop response: {}", response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("Exception: ", cause);
        super.exceptionCaught(ctx, cause);
        Channel channel = ctx.channel();
        if (channel.isActive()) ctx.close();
    }
}
