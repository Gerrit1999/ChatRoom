package com.example.entity;

import java.io.Serializable;

public class File implements Serializable {
    private Integer id;

    private String name;    // 文件名

    private String path;    // 文件路径

    private Long size;      // 文件大小

    private Image image;    // 包含的图片

    public File() {
    }

    public File(String name, String path, Long size) {
        this.name = name;
        this.path = path;
        this.size = size;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path == null ? null : path.trim();
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}