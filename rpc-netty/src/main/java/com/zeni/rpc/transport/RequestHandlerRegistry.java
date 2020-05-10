package com.zeni.rpc.transport;

import com.zeni.rpc.spi.ServiceSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RequestHandlerRegistry {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerRegistry.class);
    private Map<Integer,RequestHandler> serviceMap = new HashMap<>();
    private static RequestHandlerRegistry instance;

    private RequestHandlerRegistry(){
        Collection<RequestHandler> requestHandlers = ServiceSupport.loadAll(RequestHandler.class);
        for (RequestHandler requestHandler : requestHandlers) {
            serviceMap.put(requestHandler.type(), requestHandler);
            logger.info("Load request handler, type: {}, class: {}",requestHandler.type(), requestHandler.getClass().getCanonicalName());
        }

    }

    public static RequestHandlerRegistry getInstance(){
        if (null == instance){
            instance = new RequestHandlerRegistry();
        }
        return instance;
    }

    public RequestHandler get(int type){
        return serviceMap.get(type);
    }
}
