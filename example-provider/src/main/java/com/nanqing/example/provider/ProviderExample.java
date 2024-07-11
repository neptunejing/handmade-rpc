package com.nanqing.example.provider;

import com.nanqing.example.common.service.UserService;
import com.nanqing.rpc.*;
import com.nanqing.rpc.registry.LocalRegistry;
import com.nanqing.rpc.server.HttpServer;
import com.nanqing.rpc.server.VertxHttpServer;

public class ProviderExample {
    public static void main(String[] args) {
        // 框架初始化（使用默认配置）
        RpcApplication.init();

        // 注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());;
    }
}
