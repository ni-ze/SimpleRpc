package com.zeni.rpc;

import com.zeni.rpc.spi.ServiceSupport;

import java.io.Closeable;
import java.net.URI;
import java.util.Collection;

public interface RpcAccessPoint extends Closeable {

    /**
     * 客户端获取远程服务的引用
     *
     * @param uri          远程服务地址
     * @param serviceClass 服务接口类的class
     * @param <T>          服务接口的类型
     * @return 远程服务引用
     */
    <T> T getRemoteService(URI uri, Class<T> serviceClass);

    /**
     * 服务端注册服务实例
     *
     * @param service      实现实例
     * @param serviceClass 服务接口类class
     * @param <T>          服务接口的类型
     * @return 服务地址
     */
    <T> URI addServiceProvider(T service, Class<T> serviceClass);


    default NameService getNameService(URI nameServiceUri) {
        Collection<NameService> nameServices = ServiceSupport.loadAll(NameService.class);
        for (NameService nameService : nameServices) {
            if (nameService.supportedSchemes().contains(nameServiceUri.getScheme())) {
                nameService.connect(nameServiceUri);
                return nameService;
            }
        }
        return null;
    }

    /**
     * 服务端启动rpc框架，监听接口，提供远程服务
     *
     * @return 服务实例，用于程序停止时，安全关闭实例。
     */
    Closeable startServer() throws Exception;
}
