package com.zeni.rpc.server;

public interface ServiceProviderRegistry {
    <T> void addServiceProvider(Class<? extends T> serviceClass, T service);
}
