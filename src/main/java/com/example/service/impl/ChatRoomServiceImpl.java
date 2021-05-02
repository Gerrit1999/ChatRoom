package com.example.service.impl;

import com.example.entity.ChatRoom;
import com.example.entity.User;
import com.example.mapper.ChatRoomMapper;
import com.example.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
public class ChatRoomServiceImpl implements ChatRoomService {
    @Autowired
    ChatRoomMapper chatRoomMapper;

    @Override
    public void addChatRoom(ChatRoom chatRoom) {
        chatRoomMapper.insert(chatRoom);
    }

    @Override
    public boolean addUserToChatRoom(Integer roomId, Integer userId, String password) {
        ChatRoom chatRoom = chatRoomMapper.selectByPrimaryKey(roomId);
        if (chatRoom == null || !Objects.equals(chatRoom.getPassword(), password)) {
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
}
