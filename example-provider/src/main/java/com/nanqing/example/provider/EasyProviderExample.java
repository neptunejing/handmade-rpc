package com.nanqing.example.provider;

import com.nanqing.example.common.service.UserService;
import com.nanqing.minirpc.registry.LocalRegistry;
import com.nanqing.minirpc.server.HttpServer;
import com.nanqing.minirpc.server.VertxHttpServer;

public class EasyProviderExample {
    public static void main(String[] args) {
        // 向本地服务注册器注册
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动 web 服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);
    }
}
