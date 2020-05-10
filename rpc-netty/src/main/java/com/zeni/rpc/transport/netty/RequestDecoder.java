package com.zeni.rpc.transport.netty;

import com.zeni.rpc.transport.Command.Header;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class RequestDecoder extends CommandDecoder{
    @Override
    protected Header decodeHeader(ChannelHandlerContext ctx, ByteBuf in) {
        return new Header(in.readInt(), in.readInt(), in.readInt());
    }
}
