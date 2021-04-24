package com.example.entity;

import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;

public class Image implements Serializable {
    private final String contextPath;
    private Integer width;
    private Integer height;
    private Double proportion;//宽高比

    public Image(String filePath, String contextPath) {
        this.contextPath = contextPath;
        // 获取图片宽高
        File file = new File(filePath);
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

    public String getcontextPath() {
        return contextPath;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public Double getProportion() {
        return proportion;
    }
}
