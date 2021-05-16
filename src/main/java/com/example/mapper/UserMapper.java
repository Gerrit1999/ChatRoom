package com.example.mapper;

import com.example.entity.User;
import com.example.entity.UserExample;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    long countByExample(UserExample example);

    int deleteByExample(UserExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    List<User> selectByExample(UserExample example);

    User selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") User record, @Param("example") UserExample example);

    int updateByExample(@Param("record") User record, @Param("example") UserExample example);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    List<User> selectUsersByRoomIdActive(@Param("roomId") Integer roomId, @Param("activeTime") Integer activeTime);

    List<User> selectUsersByRoomIdNotActive(@Param("roomId") Integer roomId, @Param("activeTime") Integer activeTime);

    User selectSenderByMessageId(@Param("messageId") Integer messageId);

    User selectReceiverByMessageId(Integer messageId);
}