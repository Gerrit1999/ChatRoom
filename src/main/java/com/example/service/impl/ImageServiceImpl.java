package com.example.service.impl;

import com.example.entity.Image;
import com.example.mapper.ImageMapper;
import com.example.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageServiceImpl implements ImageService {
    @Autowired
    ImageMapper imageMapper;

    @Override
    public void addImage(Image image) {
        imageMapper.insert(image);
    }

    @Override
    public Image getImageByFileId(Integer fileId) {
        return imageMapper.selectByFileId(fileId);
    }
}
