package com.example.service;

import com.example.entity.Room;

import java.util.List;

public interface RoomService {
    void addRoom(Room room);

    boolean addUserToRoom(Integer roomId, Integer userId, String password);

    boolean removeUserFromRoom(Integer roomId, Integer userId);

    boolean judgeUserInRoom(Integer roomId, Integer userId);

    List<Room> getRoomsByUserId(Integer userId);

    Room getRoomById(Integer id);

    void updateUnread(Integer roomId, Integer userId, Integer unread);

    Integer getUnread(Integer roomId, Integer userId);

    void update(Room room);
}
