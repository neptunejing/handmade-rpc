package com.nanqing.example.provider;

import com.nanqing.example.common.model.User;
import com.nanqing.example.common.service.UserService;

public class UserServiceImpl implements UserService {

    @Override
    public User getUser(User user) {
        System.out.println("Username: " + user.getName());
        return user;
    }
}
