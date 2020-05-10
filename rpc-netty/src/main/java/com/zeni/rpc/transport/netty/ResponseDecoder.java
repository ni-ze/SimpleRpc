package com.zeni.rpc.transport.netty;

import com.zeni.rpc.transport.Command.Header;
import com.zeni.rpc.transport.Command.ResponseHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.StandardCharsets;

public class ResponseDecoder extends CommandDecoder {
    @Override
    protected Header decodeHeader(ChannelHandlerContext ctx, ByteBuf in) {
        int type = in.readInt();
        int version = in.readInt();
        int requestId = in.readInt();
        int code = in.readInt();

        int errorLength = in.readInt();
        byte[] bytes = new byte[errorLength];
        in.readBytes(bytes);

        String errorMsg = new String(bytes, StandardCharsets.UTF_8);

        return new ResponseHeader(type, version, requestId, code, errorMsg);
    }
}
