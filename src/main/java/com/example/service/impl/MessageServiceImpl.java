package com.example.service.impl;

import com.example.entity.File;
import com.example.entity.Message;
import com.example.entity.User;
import com.example.mapper.MessageMapper;
import com.example.service.FileService;
import com.example.service.MessageService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    MessageMapper messageMapper;

    @Autowired
    UserService userService;

    @Autowired
    FileService fileService;

    @Override
    public void addMessage(Message message) {
        messageMapper.insert(message);
        messageMapper.insertRoom(message.getId(), message.getRoomId());
        messageMapper.insertSender(message.getId(), message.getSender().getId());
        messageMapper.insertReceiver(message.getId(), message.getReceiverId());
        if (message.getFile() != null) {
            File file = message.getFile();
            fileService.addFile(file);
            messageMapper.insertFile(message.getId(), file.getId());
        }
    }

    @Override
    public List<Message> getIntervalMessage(Integer roomId, Integer userId, Integer intervalDays) {
        List<Message> messages = messageMapper.selectIntervalMessage(roomId, userId, intervalDays);
        for (Message message : messages) {
            User sender = userService.getSenderByMessageId(message.getId());
            message.setSender(sender);
            com.example.entity.File file = fileService.getFileByMessageId(message.getId());
            message.setFile(file);
        }
        return messages;
    }
}
