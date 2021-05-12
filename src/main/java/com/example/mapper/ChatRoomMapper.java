package com.example.mapper;

import com.example.entity.ChatRoom;
import com.example.entity.ChatRoomExample;
import com.example.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ChatRoomMapper {
    long countByExample(ChatRoomExample example);

    int deleteByExample(ChatRoomExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ChatRoom record);

    int insertSelective(ChatRoom record);

    List<ChatRoom> selectByExample(ChatRoomExample example);

    ChatRoom selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ChatRoom record, @Param("example") ChatRoomExample example);

    int updateByExample(@Param("record") ChatRoom record, @Param("example") ChatRoomExample example);

    int updateByPrimaryKeySelective(ChatRoom record);

    int updateByPrimaryKey(ChatRoom record);

    void insertUser(@Param("roomId") Integer roomId, @Param("userId") Integer userId);

    void deleteUser(@Param("roomId") Integer roomId, @Param("userId") Integer userId);

    List<User> selectUserInRoom(@Param("roomId") Integer roomId, @Param("userId") Integer userId);

    List<ChatRoom> selectChatRoomsByUserId(@Param("userId") Integer userId);

    void updateUnread(@Param("roomId") Integer roomId, @Param("userId") Integer userId, @Param("unread") Integer unread);

    Integer getUnread(@Param("roomId") Integer roomId, @Param("userId") Integer userId);
}