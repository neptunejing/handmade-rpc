package com.nanqing.rpc.registry;

import com.nanqing.rpc.config.RegistryConfig;
import com.nanqing.rpc.model.ServiceMetaInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class RegistryTest {
    final Registry registry = new EtcdRegistry();

    @Before
    public void init() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("http://127.0.0.1:2379");
        registry.init(registryConfig);
    }

    @Test
    public void testRegister() throws Exception {
        ServiceMetaInfo serviceMetaInfo1 = ServiceMetaInfo.builder().serviceName("myService").serviceVersion("1.0")
                .serviceHost("localhost").servicePort(1234).build();
        ServiceMetaInfo serviceMetaInfo2 = ServiceMetaInfo.builder().serviceName("myService").serviceVersion("1.0")
                .serviceHost("localhost").servicePort(1235).build();
        ServiceMetaInfo serviceMetaInfo3 = ServiceMetaInfo.builder().serviceName("myService").serviceVersion("2.0")
                .serviceHost("localhost").servicePort(1234).build();
        registry.register(serviceMetaInfo1);
        registry.register(serviceMetaInfo2);
        registry.register(serviceMetaInfo3);
    }

    @Test
    public void testUnRegister() throws Exception {
        ServiceMetaInfo serviceMetaInfo = ServiceMetaInfo.builder().serviceName("myService").serviceVersion("1.0").build();
        String serviceKey = serviceMetaInfo.getServiceKey();
        // 前缀搜索获取所有匹配的服务
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceKey);
        Assert.assertNotNull(serviceMetaInfoList);
    }
}
