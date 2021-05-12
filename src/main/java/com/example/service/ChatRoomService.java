package com.example.service;

import com.example.entity.ChatRoom;

import java.util.List;

public interface ChatRoomService {
    void addChatRoom(ChatRoom chatRoom);

    boolean addUserToChatRoom(Integer roomId, Integer userId, String password);

    boolean removeUserFromChatRoom(Integer roomId, Integer userId);

    boolean judgeUserInRoom(Integer roomId, Integer userId);

    List<ChatRoom> getChatRoomsByUserId(Integer userId);

    ChatRoom getChatRoomById(Integer id);

    void updateUnread(Integer roomId, Integer userId, Integer unread);

    Integer getUnread(Integer roomId, Integer userId);
}
