package com.example.service.impl;

import com.example.entity.ChatRoom;
import com.example.entity.User;
import com.example.mapper.ChatRoomMapper;
import com.example.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ChatRoomServiceImpl implements ChatRoomService {
    @Autowired
    ChatRoomMapper chatRoomMapper;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Override
    public void addChatRoom(ChatRoom chatRoom) {
        String password = chatRoom.getPassword();
        password = passwordEncoder.encode(password);
        chatRoom.setPassword(password);
        chatRoomMapper.insertSelective(chatRoom);
    }

    @Override
    public boolean addUserToChatRoom(Integer roomId, Integer userId, String password) {
        ChatRoom chatRoom = chatRoomMapper.selectByPrimaryKey(roomId);
        if (chatRoom == null || !passwordEncoder.matches(password, chatRoom.getPassword())) {
            return false;
        }
        chatRoomMapper.insertUser(roomId, userId);
        return true;
    }

    @Override
    public boolean removeUserFromChatRoom(Integer roomId, Integer userId) {
        ChatRoom chatRoom = chatRoomMapper.selectByPrimaryKey(roomId);
        if (chatRoom == null) {
            return false;
        }
        chatRoomMapper.deleteUser(roomId, userId);
        return true;
    }

    @Override
    public boolean judgeUserInRoom(Integer roomId, Integer userId) {
        List<User> users = chatRoomMapper.selectUserInRoom(roomId, userId);
        return users.size() == 1;
    }

    @Override
    public List<ChatRoom> getChatRoomsByUserId(Integer userId) {
        return chatRoomMapper.selectChatRoomsByUserId(userId);
    }

    @Override
    public ChatRoom getChatRoomById(Integer id) {
        return chatRoomMapper.selectByPrimaryKey(id);
    }
}
