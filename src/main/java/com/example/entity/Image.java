package com.example.entity;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DecimalFormat;

public class Image implements Serializable {
    private Integer id;

    private String url;

    private Integer width;

    private Integer height;

    private Double proportion;

    public Image(String path, String url) {
        this.url = url;
        // 获取图片宽高
        java.io.File file = new java.io.File(path);
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            BufferedImage src = javax.imageio.ImageIO.read(is);
            width = src.getWidth(null); // 得到图宽
            height = src.getHeight(null);// 得到图高
            proportion = Double.parseDouble(new DecimalFormat("#.00").format((double) width / height));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Double getProportion() {
        return proportion;
    }

    public void setProportion(Double proportion) {
        this.proportion = proportion;
    }
}