package com.nanqing.examplespringbootconsumer;

import com.nanqing.example.common.model.User;
import com.nanqing.example.common.service.UserService;
import com.nanqing.rpc.springboot.starter.annotation.RpcReference;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl {
    @RpcReference
    private UserService userService;

    public void test() {
        User user = new User();
        user.setName("nanqing");
        User res = userService.getUser(user);
        System.out.println("get user: " + res.getName());
    }
}
