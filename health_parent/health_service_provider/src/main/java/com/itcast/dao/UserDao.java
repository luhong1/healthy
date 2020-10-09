package com.itcast.dao;

import com.itcast.pojo.User;

public interface UserDao {
    public User findByUsername(String username);
}
