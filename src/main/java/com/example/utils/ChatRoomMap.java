package com.example.utils;

import com.example.entity.ChatRoom;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoomMap {
    private static final Map<Integer, ChatRoom> map = new ConcurrentHashMap<>();

    public static void addChatRoom(Integer port, ChatRoom chatRoom) {
        map.put(port, chatRoom);
    }

    public static Map<Integer, ChatRoom> getMap() {
        return map;
    }

    public static ChatRoom getChatRoom(Integer port) {
        return map.get(port);
    }
}
