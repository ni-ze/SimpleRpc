package com.zeni.rpc.client.stubs;

import com.zeni.rpc.client.RequestIdSupport;
import com.zeni.rpc.client.ServiceStub;
import com.zeni.rpc.client.ServiceTypes;
import com.zeni.rpc.serialize.SerializeSupport;
import com.zeni.rpc.transport.Command.Code;
import com.zeni.rpc.transport.Command.Command;
import com.zeni.rpc.transport.Command.Header;
import com.zeni.rpc.transport.Command.ResponseHeader;
import com.zeni.rpc.transport.Transport;

import java.util.concurrent.ExecutionException;

public abstract class AbstractStub implements ServiceStub {
    protected Transport transport;

    protected byte [] invokeRemote(RpcRequest rpcRequest){
        Header header = new Header(ServiceTypes.TYPE_RPC_REQUEST, 1, RequestIdSupport.next());
        byte[] payload = SerializeSupport.serialize(rpcRequest);

        Command requestCommand = new Command(header, payload);

        try{
            Command responseCommand = transport.send(requestCommand).get();
            ResponseHeader responseHeader = (ResponseHeader) responseCommand.getHeader();
            if (responseHeader.getCode() == Code.SUCCESS.getCode()){
                return responseCommand.getPayload();
            }else {
                throw new Exception(responseHeader.getError());
            }


        }catch (ExecutionException e){
            throw new RuntimeException(e.getCause());
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }

    }

    @Override
    public void setTransport(Transport transport) {
        this.transport = transport;
    }
}
