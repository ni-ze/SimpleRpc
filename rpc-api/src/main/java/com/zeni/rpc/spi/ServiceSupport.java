package com.zeni.rpc.spi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ServiceSupport {
    private static final Map<String,Object> singletonServices = new HashMap<>();

    /**
     *
     * @param service
     * @param <S>
     * @return 返回一个实现service接口的实现类
     */
    public synchronized static <S> S load(Class<S> service){
        return StreamSupport.stream(ServiceLoader.load(service).spliterator(), false)
                .map(ServiceSupport::singletonFilter).findFirst().orElseThrow(ServiceLoadException::new);
    }

    /**
     *
     * @param service
     * @param <S>
     * @return 所有实现service接口的实现类
     */
    public synchronized static <S> Collection<S> loadAll(Class<S> service){
        return StreamSupport.stream(ServiceLoader.load(service).spliterator(), false)
                .map(ServiceSupport::singletonFilter).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private static  <S> S singletonFilter(S service){
        if (service.getClass().isAnnotationPresent(Singleton.class)){
            String canonicalName = service.getClass().getCanonicalName();
            Object singletonInstance = singletonServices.putIfAbsent(canonicalName, service);

            return singletonInstance == null ? service : (S)singletonInstance;
        }else {
            return service;
        }
    }

}
