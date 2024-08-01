package com.nanqing.rpc.springboot.starter.bootstrap;

import com.nanqing.rpc.RpcApplication;
import com.nanqing.rpc.config.RpcConfig;
import com.nanqing.rpc.server.tcp.VertxTcpServer;
import com.nanqing.rpc.springboot.starter.annotation.EnableRpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

@Slf4j
public class BootstrapperSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        // 获取 EnableRpc 注解的属性值
        boolean needServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName())
                .get("needServer");

        // Rpc 框架初始化
        RpcApplication.init();

        // 全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        if (needServer) {
            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            vertxTcpServer.doStart(rpcConfig.getServerPort());
            log.info("使用 RpcProviderBootstrap，启动 server");
            return new String[]{RpcProviderBootstrap.class.getName()};
        } else {
            log.info("使用 RpcProviderBootstrap，不启动 server");
            return new String[]{RpcConsumerBootstrap.class.getName()};
        }
    }
}
