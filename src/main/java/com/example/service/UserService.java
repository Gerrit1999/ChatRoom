package com.example.service;

import com.example.entity.User;

import java.util.List;

public interface UserService {
    User getUserById(Integer id);

    String getUsernameById(Integer id);

    User getUserByUsername(String username);

    boolean addUser(User user);

    User findUserByUsername(String username);

    List<User> getUsersByRoomId(int roomId);

    User getSenderByMessageId(Integer messageId);
}
