package com.example.service.impl;

import com.example.entity.File;
import com.example.entity.Message;
import com.example.mapper.MessageMapper;
import com.example.service.FileService;
import com.example.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    MessageMapper messageMapper;

    @Autowired
    FileService fileService;

    @Override
    public void addMessage(Message message) {
        messageMapper.insert(message);
        if (message.getFile() != null) {
            File file = message.getFile();
            fileService.addFile(file);
            messageMapper.insertFile(message.getId(), file.getId());
        }
    }
}
