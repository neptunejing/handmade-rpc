package com.nanqing.example.consumer;

import com.nanqing.example.common.service.UserService;
import com.nanqing.rpc.proxy.ServiceProxyFactory;

public class ConsumerExample {
    public static void main(String[] args) {
        // 获取代理（传入接口）
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        // 调用
        var number = userService.getNumer();
        System.out.println(number);
    }
}
