package com.zeni.rpc.transport.Command;

public class Header {
    private int requestId;

    /**
     * 标示协议版本，可用于后续协议升级，判定支持版本才响应
     */
    private int version;
    /**
     * 标识这条命令的类型，用于接收方识别是什么命令，路由到对应处理类中。
     */
    private int type;

    public Header(int type, int version, int requestId) {
        this.requestId = requestId;
        this.version = version;
        this.type = type;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int length() {
        return Integer.BYTES + Integer.BYTES + Integer.BYTES;
    }
}
