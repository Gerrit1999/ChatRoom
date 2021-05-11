package com.example.service;

import com.example.entity.Image;

public interface ImageService {
    void addImage(Image image);

    Image getImageByFileId(Integer id);
}
