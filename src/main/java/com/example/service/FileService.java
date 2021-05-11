package com.example.service;

import com.example.entity.File;
import org.springframework.stereotype.Service;

public interface FileService {
    void addFile(File file);

    File getFileByMessageId(Integer messageId);
}
