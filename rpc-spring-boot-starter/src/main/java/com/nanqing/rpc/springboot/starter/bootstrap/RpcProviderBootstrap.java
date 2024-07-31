package com.nanqing.rpc.springboot.starter.bootstrap;

import com.nanqing.rpc.RpcApplication;
import com.nanqing.rpc.config.RegistryConfig;
import com.nanqing.rpc.config.RpcConfig;
import com.nanqing.rpc.model.ServiceMetaInfo;
import com.nanqing.rpc.registry.LocalRegistry;
import com.nanqing.rpc.registry.Registry;
import com.nanqing.rpc.registry.RegistryFactory;
import com.nanqing.rpc.springboot.starter.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * RPC 服务提供者启动
 */
@Slf4j
public class RpcProviderBootstrap implements BeanPostProcessor {
    /**
     * Bean 初始化后执行
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        RpcService rpcService = clazz.getAnnotation(RpcService.class);
        if (rpcService != null) {
            // 注册流程
            // 1. 获取服务基本信息
            Class<?> interfaceClass = rpcService.interfaceClass();
            // 默认值处理
            if (interfaceClass == void.class) {
                interfaceClass = clazz.getInterfaces()[0];
            }
            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();
            // 2. 注册服务
            // 本地注册
            LocalRegistry.register(serviceName, clazz);
            final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            // 注册到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = ServiceMetaInfo.builder()
                    .serviceName(serviceName)
                    .serviceVersion(serviceVersion)
                    .serviceHost(rpcConfig.getServerHost())
                    .servicePort(rpcConfig.getServerPort())
                    .build();
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + " 服务注册失败", e);
            }
        }

        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
