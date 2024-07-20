package com.nanqing.example.consumer;

import com.nanqing.example.common.model.User;
import com.nanqing.example.common.service.UserService;
import com.nanqing.rpc.proxy.ServiceProxyFactory;

public class ConsumerExample {
    public static void main(String[] args) {
        // 获取代理（传入接口）
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("nanqing");

        // 调用
        var num = userService.getNumer();
        System.out.println("get number: " + num);

        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println("get user: " + newUser.getName());
        } else {
            System.out.println("user is null");
        }
    }
}
