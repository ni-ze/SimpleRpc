package com.zeni.rpc.serialize;

import com.zeni.rpc.spi.ServiceSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SerializeSupport {
    private static final Logger logger = LoggerFactory.getLogger(SerializeSupport.class);
    private static Map<Class<?>/*序列化对象类型*/, Serializer<?>/*序列化实现*/> serializerMap = new HashMap<Class<?>, Serializer<?>>();
    private static Map<Byte/*序列化实现类型*/, Class<?>/*序列化对象类型*/> typeMap = new HashMap<Byte, Class<?>>();

    static {
        for (Serializer serializer :ServiceSupport.loadAll(Serializer.class)) {
            registerType(serializer.type(), serializer.getSerializeClass(), serializer);
            logger.info("Found serializer, class: {}, type: {}.", serializer.getSerializeClass().getCanonicalName(), serializer.type());
        }
    }

    public static void registerType(byte type, Class clazz, Serializer serializer){
        serializerMap.put(clazz, serializer);
        typeMap.put(type, clazz);
    }


    public static byte parseEntryType(byte[] buffer) {
        return buffer[0];
    }

    public static <E> E parse(byte[] buffer) {
        return parse(buffer, 0, buffer.length);
    }

    public static <E> E parse(byte[] buffer, int offset, int length) {
        byte type = parseEntryType(buffer);
        @SuppressWarnings("unchecked")
        Class<E> clazz = (Class<E>) typeMap.get(type);

        if (clazz == null) {
            throw new SerializeException(String.format("Unknown entry type: %d!", type));
        }

        return parse(buffer, offset + 1, length - 1, clazz);
    }

    @SuppressWarnings("unchecked")
    public static <E> E parse(byte[] buffer, int offset, int length, Class<E> clazz) {
        Serializer<E> serializer = (Serializer<E>) serializerMap.get(clazz);
        Object entry = serializer.parse(buffer, offset, length);

        if (clazz.isAssignableFrom(entry.getClass())) {
            return (E) entry;
        } else {
            throw new SerializeException("Type mismatch");
        }
    }

    public static <E> byte[] serialize(E entry) {
        @SuppressWarnings("unchecked")
        Serializer<E> serializer = (Serializer<E>) serializerMap.get(entry.getClass());

        if (serializer == null) {
            throw new SerializeException(String.format("Unknown entry class type: %s", entry.getClass().toString()));
        }

        byte[] bytes = new byte[serializer.size(entry) + 1];
        bytes[0] = serializer.type();
        serializer.serialize(entry, bytes, 1, bytes.length - 1);
        return bytes;
    }
}
