package com.example.service.impl;

import com.example.entity.File;
import com.example.entity.Image;
import com.example.mapper.FileMapper;
import com.example.service.FileService;
import com.example.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl implements FileService {
    @Autowired
    FileMapper fileMapper;

    @Autowired
    ImageService imageService;

    @Override
    public void addFile(File file) {
        fileMapper.insert(file);
        if (file.getImage() != null) {
            Image image = file.getImage();
            imageService.addImage(image);
            fileMapper.insertImage(file.getId(), image.getId());
        }
    }
}
