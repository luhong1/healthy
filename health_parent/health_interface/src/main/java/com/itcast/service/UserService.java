package com.itcast.service;

import com.itcast.pojo.User;

public interface UserService {
    public User findByUsername(String username);
}
