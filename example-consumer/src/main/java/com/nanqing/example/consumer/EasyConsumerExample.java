package com.nanqing.example.consumer;

import com.nanqing.example.common.model.User;
import com.nanqing.example.common.service.UserService;
import com.nanqing.minirpc.proxy.ServiceProxyFactory;


public class EasyConsumerExample {

    public static void main(String[] args) {
        // 动态代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("nanqing");

        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user is null");
        }
    }
}
