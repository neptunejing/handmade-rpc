package com.nanqing.rpc.config;

import com.nanqing.rpc.fault.retry.RetryStrategyKeys;
import com.nanqing.rpc.fault.tolerant.TolerantStrategy;
import com.nanqing.rpc.fault.tolerant.TolerantStrategyKeys;
import com.nanqing.rpc.loadbalancer.LoadBalancerKeys;
import com.nanqing.rpc.serializer.SerializerKeys;
import lombok.Data;

/**
 * RPC 框架配置
 */
@Data
public class RpcConfig {
    /**
     * 名称
     */
    private String name = "handmade-rpc";

    /**
     * 版本号
     */
    private String version = "1.0.0";

    /**
     * 服务器主机名
     */
    private String serverHost = "localhost";

    /**
     * 服务器端口号
     */
    private Integer serverPort = 8080;

    /**
     * mock
     */
    private boolean mock = false;

    /**
     * 序列化器
     */
    private String serializer = SerializerKeys.JDK;

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();

    /**
     * 负载均衡器配置
     */
    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 重试策略配置
     */
    private String retryStrategy = RetryStrategyKeys.NO;

    /**
     * 容错策略配置
     */
    private String tolerantStrategy = TolerantStrategyKeys.FAIL_FAST;
}
