package com.example.mapper;

import com.example.entity.Message;
import com.example.entity.MessageExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageMapper {
    long countByExample(MessageExample example);

    int deleteByExample(MessageExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Message record);

    int insertSelective(Message record);

    List<Message> selectByExample(MessageExample example);

    Message selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Message record, @Param("example") MessageExample example);

    int updateByExample(@Param("record") Message record, @Param("example") MessageExample example);

    int updateByPrimaryKeySelective(Message record);

    int updateByPrimaryKey(Message record);

    void insertFile(@Param("messageId") Integer messageId, @Param("fileId") Integer fileId);

    void insertRoom(@Param("messageId") Integer messageId, @Param("roomId") Integer roomId);

    void insertSender(@Param("messageId") Integer messageId, @Param("userId") Integer userId);

    void insertReceiver(@Param("messageId") Integer messageId, @Param("userId") Integer userId);

    List<Message> selectIntervalMessage(@Param("roomId") Integer roomId, @Param("userId") Integer userId, @Param("intervalDays") Integer intervalDays);
}