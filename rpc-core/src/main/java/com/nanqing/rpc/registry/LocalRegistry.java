package com.nanqing.rpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalRegistry {
    // 注册信息存储
    private static final Map<String, Class<?>> registry = new ConcurrentHashMap<>();

    // 注册服务
    public static void register(String serviceName, Class<?> implClass) {
        registry.put(serviceName, implClass);
    }

    // 获取服务
    public static Class<?> get(String serviceName) {
        return registry.get(serviceName);
    }

    // 删除服务
    public static void remove(String serviceName) {
        registry.remove(serviceName);
    }
}
