package com.example.service;

import com.example.entity.ChatRoom;

public interface ChatRoomService {
    void addChatRoom(ChatRoom chatRoom);

    boolean addUserToChatRoom(Integer roomId, Integer userId, String password);

    boolean removeUserFromChatRoom(Integer roomId, Integer userId);

    boolean judgeUserInRoom(Integer roomId, Integer userId);
}
