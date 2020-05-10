package com.zeni.server;

import com.zeni.rpc.NameService;
import com.zeni.rpc.RpcAccessPoint;
import com.zeni.rpc.spi.ServiceSupport;
import com.zeni.service.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.net.URI;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws Exception {
        HelloServiceImpl helloService = new HelloServiceImpl();

        File tempDirFile = new File(System.getProperty("java.io.tmpdir"));
        File file = new File(tempDirFile, "simple_rpc_name_service.data");

        logger.info("start RpcAccessPoint.");

        try (RpcAccessPoint accessPoint = ServiceSupport.load(RpcAccessPoint.class);
             Closeable ignored = accessPoint.startServer()) {
            String serviceName = HelloService.class.getCanonicalName();

            logger.info("register service:{} into accessPoint.", serviceName);
            URI serviceProviderUri = accessPoint.addServiceProvider(helloService, HelloService.class);

            NameService nameService = accessPoint.getNameService(file.toURI());
            assert nameService != null;
            logger.info("register service: {} into to nameService.", serviceName);
            nameService.registerService(serviceName, serviceProviderUri);

            logger.info("enter any key to exit: ");
            System.in.read();
            logger.info("Bye!");
        }


    }

}
