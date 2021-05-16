package com.example.service.impl;

import com.example.entity.User;
import com.example.entity.UserExample;
import com.example.mapper.UserMapper;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Override
    public User getUserById(Integer id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public String getUsernameById(Integer id) {
        User user = getUserById(id);
        return user.getUsername();
    }

    @Override
    public User getUserByUsername(String username) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andUsernameEqualTo(username);
        List<User> users = userMapper.selectByExample(userExample);
        return (users == null || users.isEmpty()) ? null : users.get(0);
    }

    @Override
    public boolean addUser(User user) {
        String password = user.getPassword();
        password = passwordEncoder.encode(password);
        user.setPassword(password);
        int cnt = userMapper.insertSelective(user);
        return cnt == 1;
    }

    @Override
    public User findUserByUsername(String username) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andUsernameEqualTo(username);
        List<User> users = userMapper.selectByExample(userExample);
        return (users == null || users.isEmpty()) ? null : users.get(0);
    }

    @Override
    public List<User> getUsersByRoomIdActive(Integer roomId, Integer activeTime) {
        return userMapper.selectUsersByRoomIdActive(roomId, activeTime);
    }

    @Override
    public List<User> getUsersByRoomIdNotActive(Integer roomId, Integer activeTime) {
        return userMapper.selectUsersByRoomIdNotActive(roomId, activeTime);
    }

    @Override
    public User getSenderByMessageId(Integer messageId) {
        return userMapper.selectSenderByMessageId(messageId);
    }
    @Override
    public User getReceiverByMessageId(Integer messageId) {
        return userMapper.selectReceiverByMessageId(messageId);
    }

    @Override
    public void updateRecentActiveTime(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        user.setRecentActiveTime(new Date());
        userMapper.updateByPrimaryKey(user);
    }
}
