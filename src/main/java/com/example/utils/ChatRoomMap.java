package com.example.utils;

import com.example.entity.ChatRoom;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoomMap {

    private static final Map<Integer, ChatRoom> chatRoomMap = new ConcurrentHashMap<>();

    public static void addChatRoom(Integer roomId, ChatRoom chatRoom) {
        chatRoomMap.put(roomId, chatRoom);
    }

    public static ChatRoom getChatRoom(Integer roomId) {
        return chatRoomMap.get(roomId);
    }

    public static boolean containsChatRoom(Integer roomId) {
        return chatRoomMap.containsKey(roomId);
    }

    public static Map<Integer, ChatRoom> getChatRoomMap() {
        return chatRoomMap;
    }
}
