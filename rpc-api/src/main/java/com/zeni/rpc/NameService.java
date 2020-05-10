package com.zeni.rpc;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

/**
 * 注册中心
 */
public interface NameService {
    /**
     *所有支持的协议
     */
    Collection<String> supportedSchemes();

    /**
     * 连接注册中心
     */
    void connect(URI nameServiceUri);

    /**
     * 注册服务
     * @param serviceName 服务实例
     * @param uri 服务地址
     * @throws IOException
     */
    void registerService(String serviceName, URI uri) throws IOException;

    /**
     * 查询服务地址
     * @param serviceName 服务实例
     * @return 服务地址
     */
    URI lookupService(String serviceName) throws IOException;
}
