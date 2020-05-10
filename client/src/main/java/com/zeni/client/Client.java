package com.zeni.client;

import com.zeni.rpc.NameService;
import com.zeni.rpc.RpcAccessPoint;
import com.zeni.rpc.spi.ServiceSupport;
import com.zeni.service.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws IOException {
        String serviceName = HelloService.class.getCanonicalName();

        File tempDirFile = new File(System.getProperty("java.io.tmpdir"));
        File file = new File(tempDirFile, "simple_rpc_name_service.data");

        String name = "Master MQ";

        try (RpcAccessPoint accessPoint = ServiceSupport.load(RpcAccessPoint.class)){
            NameService nameService = accessPoint.getNameService(file.toURI());
            assert nameService != null;

            URI uri = nameService.lookupService(serviceName);
            assert uri != null;

            logger.info("find service:{}, with provider: {}",serviceName, uri);
            HelloService helloService = accessPoint.getRemoteService(uri, HelloService.class);


            logger.info("request service, name: {}",serviceName);
            String result = helloService.hello(name);
            logger.info("response:{}, from remote service: {}",result, helloService);
        }

    }
}
