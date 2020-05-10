package com.zeni.rpc.transport.netty;

import com.zeni.rpc.transport.Command.Header;
import com.zeni.rpc.transport.Command.ResponseHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.StandardCharsets;

public class ResponseEncoder extends CommandEncoder {
    @Override
    protected void encodeHeader(ChannelHandlerContext ctx, Header header, ByteBuf out) throws Exception{
        super.encodeHeader(ctx, header, out);
        if (header instanceof ResponseHeader){
            ResponseHeader responseHeader = (ResponseHeader) header;
            out.writeInt(responseHeader.getCode());

            int errorMsgLength = responseHeader.length() - (Integer.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES);

            out.writeInt(errorMsgLength);
            out.writeBytes(responseHeader.getError() == null ? new byte[0] : responseHeader.getError().getBytes(StandardCharsets.UTF_8));
        }else {
            throw new Exception(String.format("Invalid header type: %s", header.getClass().getCanonicalName()));
        }
    }
}
