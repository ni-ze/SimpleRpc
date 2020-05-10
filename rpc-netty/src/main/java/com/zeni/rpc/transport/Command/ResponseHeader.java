package com.zeni.rpc.transport.Command;

import java.nio.charset.StandardCharsets;

public class ResponseHeader extends Header {
    private int code;
    private String error;

    public ResponseHeader(int type, int version, int requestId){
        this(type, version, requestId, Code.SUCCESS.getCode(), null);
    }

    public ResponseHeader(int type, int version, int requestId, int code, String error) {
        super(requestId, version, type);
        this.code = code;
        this.error = error;
    }

    @Override
    public int length(){
        return Integer.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES +
                Integer.BYTES +
                (error == null ? 0 : error.getBytes(StandardCharsets.UTF_8).length);
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
