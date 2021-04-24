package com.example.entity;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable {
    private final String date;// 消息时间

    private String msg;// 消息内容

    private String fontSize;// 字体大小

    private String fontWeight;// 字体粗细

    private String fontStyle;// 字体风格

    private String sourceAddress;// 消息源IP

    private Integer sourcePort;// 消息源端口

    private File file = null;

    private Image image = null;

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public Message() {
        date = simpleDateFormat.format(new Date());
    }

    public Message(String msg, String sourceAddress, Integer sourcePort) {
        date = simpleDateFormat.format(new Date());
        this.msg = msg;
        this.sourceAddress = sourceAddress;
        this.sourcePort = sourcePort;
    }

    public Message(String msg, String sourceAddress, Integer sourcePort, File file) {
        date = simpleDateFormat.format(new Date());
        this.msg = msg;
        this.sourceAddress = sourceAddress;
        this.sourcePort = sourcePort;
        this.file = file;
    }


    public String getDate() {
        return date;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontWeight() {
        return fontWeight;
    }

    public void setFontWeight(String fontWeight) {
        this.fontWeight = fontWeight;
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public Integer getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(Integer sourcePort) {
        this.sourcePort = sourcePort;
    }

    public File getFile() {
        return file;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(String filePath, String contextPath) {
        image = new Image(filePath, contextPath);
    }

    @Override
    public String toString() {
        return "Message{" +
                "date='" + date + '\'' +
                ", msg='" + msg + '\'' +
                ", fontSize='" + fontSize + '\'' +
                ", fontWeight='" + fontWeight + '\'' +
                ", fontStyle='" + fontStyle + '\'' +
                ", sourceAddress='" + sourceAddress + '\'' +
                ", sourcePort=" + sourcePort +
                ", file=" + file +
                ", image=" + image +
                '}';
    }
}
