package com.nanqing.rpc;

import com.nanqing.rpc.config.RegistryConfig;
import com.nanqing.rpc.config.RpcConfig;
import com.nanqing.rpc.constant.RpcConstant;
import com.nanqing.rpc.registry.Registry;
import com.nanqing.rpc.registry.RegistryFactory;
import com.nanqing.rpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC 框架应用
 * 启动时加载全局配置
 */
@Slf4j
public class RpcApplication {
    private static volatile RpcConfig rpcConfig;

    /**
     * 指定配置完成框架初始化
     */
    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("RpcApplication init, config: {}", newRpcConfig.toString());
        // 注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig(); // 先从配置读取
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry()); // 根据配置获取实例
        registry.init(registryConfig);
        log.info("Registry init, config: {}", registry);
    }

    /**
     * 初始化
     */
    public static void init() {
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            // 读取失败，使用默认配置兜底
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * 获取配置（双检锁单例模式）
     */
    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
