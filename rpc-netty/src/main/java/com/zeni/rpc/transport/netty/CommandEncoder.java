package com.zeni.rpc.transport.netty;

import com.zeni.rpc.transport.Command.Command;
import com.zeni.rpc.transport.Command.Header;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public abstract class CommandEncoder extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (!(msg instanceof Command)){
            throw new Exception(String.format("Unknown type: %s", msg.getClass().getCanonicalName()));
        }

        Command command = (Command) msg;
        out.writeInt(Integer.BYTES + command.getHeader().length()+ command.getPayload().length);
        encodeHeader(ctx, command.getHeader(), out);
        out.writeBytes(command.getPayload());
    }

    protected void encodeHeader(ChannelHandlerContext ctx, Header header, ByteBuf out)  throws Exception{
        out.writeInt(header.getType());
        out.writeInt(header.getVersion());
        out.writeInt(header.getRequestId());
    }
}
