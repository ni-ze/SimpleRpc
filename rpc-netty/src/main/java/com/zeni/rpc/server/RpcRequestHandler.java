package com.zeni.rpc.server;

import com.zeni.rpc.client.ServiceTypes;
import com.zeni.rpc.client.stubs.RpcRequest;
import com.zeni.rpc.serialize.SerializeSupport;
import com.zeni.rpc.spi.Singleton;
import com.zeni.rpc.transport.Command.Code;
import com.zeni.rpc.transport.Command.Command;
import com.zeni.rpc.transport.Command.Header;
import com.zeni.rpc.transport.Command.ResponseHeader;
import com.zeni.rpc.transport.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class RpcRequestHandler implements RequestHandler, ServiceProviderRegistry {
    private final static Logger logger = LoggerFactory.getLogger(RpcRequestHandler.class);
    private Map<String/*service name*/, Object/*service provider*/> serviceProvider = new HashMap<>();

    @Override
    public Command handler(Command requestCommand) {
        Header header = requestCommand.getHeader();

        RpcRequest request = SerializeSupport.parse(requestCommand.getPayload());
        try{
            Object provider = serviceProvider.get(request.getInterfaceName());
            if (provider != null){
                String arg = SerializeSupport.parse(request.getSerializedArguments());
                Method method = provider.getClass().getDeclaredMethod(request.getMethodName(), String.class);
                String result = (String) method.invoke(provider, arg);

                return new Command(new ResponseHeader(type(),header.getVersion(), header.getRequestId()),SerializeSupport.serialize(result));
            }
            logger.error("No service Provider of {}#{}",request.getInterfaceName(), request.getMethodName());
            return new Command(new ResponseHeader(type(), header.getVersion(), header.getRequestId(), Code.NO_PROVIDER.getCode(), "No Provider!"), new byte[0]);

        }catch (Exception e){
            logger.warn("Exception: ", e);
            return new Command(new ResponseHeader(type(), header.getVersion(), header.getRequestId(), Code.UNKNOWN_ERROR.getCode(), e.getMessage()), new byte[0]);
        }

    }

    @Override
    public int type() {
        return ServiceTypes.TYPE_RPC_REQUEST;
    }

    @Override
    public <T> void addServiceProvider(Class<? extends T> serviceClass, T service) {
        serviceProvider.put(serviceClass.getCanonicalName(), service);
        logger.info("Add service name:{}, provider:{}", serviceClass.getCanonicalName(), serviceProvider.getClass().getCanonicalName());
    }
}
