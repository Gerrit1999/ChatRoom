package com.example.service;

import com.example.entity.Message;
import com.example.entity.User;

import java.util.List;

public interface MessageService {
    void addMessage(Message message);

    List<Message> getIntervalMessage(Integer roomId, Integer userId, Integer intervalDays);
}
