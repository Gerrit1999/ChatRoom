package com.example.mapper;

import com.example.entity.Room;
import com.example.entity.RoomExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoomMapper {
    long countByExample(RoomExample example);

    int deleteByExample(RoomExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Room record);

    int insertSelective(Room record);

    List<Room> selectByExample(RoomExample example);

    Room selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Room record, @Param("example") RoomExample example);

    int updateByExample(@Param("record") Room record, @Param("example") RoomExample example);

    int updateByPrimaryKeySelective(Room record);

    int updateByPrimaryKey(Room record);

    void insertUser(@Param("roomId") Integer roomId, @Param("userId") Integer userId);

    void deleteUser(@Param("roomId") Integer roomId, @Param("userId") Integer userId);

    Integer selectUserInRoom(@Param("roomId") Integer roomId, @Param("userId") Integer userId);

    List<Room> selectChatRoomsByUserId(@Param("userId") Integer userId);

    void updateUnread(@Param("roomId") Integer roomId, @Param("userId") Integer userId, @Param("unread") Integer unread);

    Integer getUnread(@Param("roomId") Integer roomId, @Param("userId") Integer userId);
}