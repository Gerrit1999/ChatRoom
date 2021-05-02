package com.example.service;

import com.example.entity.User;

public interface UserService {
    User getUserById(Integer id);

    String getUsernameById(Integer id);

    User getUserByUsername(String username);
}
