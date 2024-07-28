package com.nanqing.example.provider;

import com.nanqing.example.common.service.UserService;
import com.nanqing.rpc.*;
import com.nanqing.rpc.config.RegistryConfig;
import com.nanqing.rpc.config.RpcConfig;
import com.nanqing.rpc.model.ServiceMetaInfo;
import com.nanqing.rpc.registry.LocalRegistry;
import com.nanqing.rpc.registry.Registry;
import com.nanqing.rpc.registry.RegistryFactory;
import com.nanqing.rpc.server.tcp.VertxTcpServer;

public class ProviderExample {
    public static void main(String[] args) {
        // 框架初始化（使用默认配置）
        RpcApplication.init();

        // 注册服务到本地服务注册器
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        // 注册服务到注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = ServiceMetaInfo.builder()
                .serviceName(serviceName)
                .serviceHost(rpcConfig.getServerHost())
                .servicePort(rpcConfig.getServerPort())
                .build();
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 启动 TCP 服务
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
