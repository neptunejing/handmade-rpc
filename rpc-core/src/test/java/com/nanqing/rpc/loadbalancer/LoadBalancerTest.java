package com.nanqing.rpc.loadbalancer;

import com.nanqing.rpc.model.ServiceMetaInfo;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 负载均衡器测试
 */
public class LoadBalancerTest {
    private final LoadBalancer loadBalancer = new ConsistentHashLoadBalancer();

    @Test
    public void select() {
        // mock 请求参数
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", "mock");
        // mock 服务列表
        ServiceMetaInfo serviceMetaInfo1 = ServiceMetaInfo.builder().serviceName("hello").serviceVersion("1.0")
                .serviceHost("localhost").servicePort(90).build();
        ServiceMetaInfo serviceMetaInfo2 = ServiceMetaInfo.builder().serviceName("world").serviceVersion("1.0")
                .serviceHost("localhost").servicePort(1234).build();
        ServiceMetaInfo serviceMetaInfo3 = ServiceMetaInfo.builder().serviceName("java").serviceVersion("2.0")
                .serviceHost("localhost").servicePort(80).build();
        List<ServiceMetaInfo> serviceMetaInfoList = Arrays.asList(serviceMetaInfo1, serviceMetaInfo2, serviceMetaInfo3);
        // 连续调用 3 次
        ServiceMetaInfo serviceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
        System.out.println(serviceMetaInfo);
        Assert.assertNotNull(serviceMetaInfo);
        serviceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
        System.out.println(serviceMetaInfo);
        Assert.assertNotNull(serviceMetaInfo);
        serviceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
        System.out.println(serviceMetaInfo);
        Assert.assertNotNull(serviceMetaInfo);
    }
}
