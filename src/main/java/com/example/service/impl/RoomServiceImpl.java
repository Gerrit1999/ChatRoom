package com.example.service.impl;

import com.example.entity.Room;
import com.example.mapper.RoomMapper;
import com.example.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    RoomMapper roomMapper;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Override
    public void addRoom(Room room) {
        String password = room.getPassword();
        password = passwordEncoder.encode(password);
        room.setPassword(password);
        roomMapper.insertSelective(room);
    }

    @Override
    public boolean addUserToRoom(Integer roomId, Integer userId, String password) {
        Room room = roomMapper.selectByPrimaryKey(roomId);
        if (room == null || !passwordEncoder.matches(password, room.getPassword())) {
            return false;
        }
        roomMapper.insertUser(roomId, userId);
        return true;
    }

    @Override
    public boolean removeUserFromRoom(Integer roomId, Integer userId) {
        Room room = roomMapper.selectByPrimaryKey(roomId);
        if (room == null) {
            return false;
        }
        roomMapper.deleteUser(roomId, userId);
        return true;
    }

    @Override
    public boolean judgeUserInRoom(Integer roomId, Integer userId) {
        return roomMapper.selectUserInRoom(roomId, userId) == 1;
    }

    @Override
    public List<Room> getRoomsByUserId(Integer userId) {
        return roomMapper.selectChatRoomsByUserId(userId);
    }

    @Override
    public Room getRoomById(Integer id) {
        return roomMapper.selectByPrimaryKey(id);
    }

    @Override
    public void updateUnread(Integer roomId, Integer userId, Integer unread) {
        roomMapper.updateUnread(roomId, userId, unread);
    }

    @Override
    public Integer getUnread(Integer roomId, Integer userId) {
        return roomMapper.getUnread(roomId, userId);
    }

    @Override
    public void update(Room room) {
        roomMapper.updateByPrimaryKeySelective(room);
    }
}
