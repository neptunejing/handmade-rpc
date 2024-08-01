package com.nanqing.examplespringbootprovider;

import com.nanqing.example.common.model.User;
import com.nanqing.example.common.service.UserService;
import com.nanqing.rpc.springboot.starter.annotation.RpcService;
import org.springframework.stereotype.Service;

@Service
@RpcService
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
}
