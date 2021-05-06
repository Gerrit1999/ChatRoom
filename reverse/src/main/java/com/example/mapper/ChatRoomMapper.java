package com.example.mapper;

import com.example.entity.ChatRoom;
import com.example.entity.ChatRoomExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

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
}