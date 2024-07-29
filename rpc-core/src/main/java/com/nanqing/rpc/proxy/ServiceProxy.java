package com.nanqing.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import com.nanqing.rpc.RpcApplication;
import com.nanqing.rpc.config.RpcConfig;
import com.nanqing.rpc.constant.RpcConstant;
import com.nanqing.rpc.fault.retry.RetryStrategy;
import com.nanqing.rpc.fault.retry.RetryStrategyFactory;
import com.nanqing.rpc.fault.tolerant.TolerantStrategy;
import com.nanqing.rpc.fault.tolerant.TolerantStrategyFactory;
import com.nanqing.rpc.loadbalancer.LoadBalancer;
import com.nanqing.rpc.loadbalancer.LoadBalancerFactory;
import com.nanqing.rpc.model.RpcRequest;
import com.nanqing.rpc.model.RpcResponse;
import com.nanqing.rpc.model.ServiceMetaInfo;
import com.nanqing.rpc.registry.Registry;
import com.nanqing.rpc.registry.RegistryFactory;
import com.nanqing.rpc.server.tcp.VertxTcpClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        try {
            // 从注册中心获取服务提供者的地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = ServiceMetaInfo.builder()
                    .serviceName(serviceName)
                    .serviceVersion(RpcConstant.DEFAULT_SERVICE_VERSION)
                    .build();
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("暂无无服务地址");
            }

            // 负载均衡
            LoadBalancer loadBalancer = LoadBalancerFactory.getLoadBalancer(rpcConfig.getLoadBalancer());
            // 将调用的方法名作为负载均衡参数
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("methodName", rpcRequest.getMethodName());
            ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
            System.out.println(selectedServiceMetaInfo.getServiceAddress());

            // 使用重试机制发出 TCP 请求
            RpcResponse rpcResponse;
            try {
                RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
                rpcResponse = retryStrategy.doRetry(() ->
                        VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo)
                );
            } catch (Exception e) {
                // 容错机制
                TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
                rpcResponse = tolerantStrategy.doTolerant(null, e);
            }
            return rpcResponse.getData();
        } catch (Exception e) {
            throw new RuntimeException("调用失败", e);
        }
    }
}
