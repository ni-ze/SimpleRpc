package com.zeni.rpc.transport.netty;

import com.zeni.rpc.transport.Command.Header;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class RequestEncoder extends CommandEncoder {
    @Override
    protected void encodeHeader(ChannelHandlerContext ctx, Header header, ByteBuf out)  throws Exception{
        super.encodeHeader(ctx, header, out);
    }
}
