package com.nanqing.example.common.service;

import com.nanqing.example.common.model.User;

public interface UserService {

    User getUser(User user);

    default short getNumer() {
        return 1;
    }
}
