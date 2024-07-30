package com.nanqing.example.provider;

import com.nanqing.example.common.service.UserService;
import com.nanqing.rpc.bootstrap.ProviderBootstrap;
import com.nanqing.rpc.model.ServiceRegisterInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务提供者示例
 */
public class ProviderExample {
    public static void main(String[] args) {
        // 待注册服务列表
        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo<?> serviceRegisterInfo = new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);

        // 服务提供者初始化
        ProviderBootstrap.init(serviceRegisterInfoList);
    }
}
