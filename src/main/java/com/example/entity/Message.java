package com.example.entity;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Message implements Serializable {
    private final String date;// 消息时间

    private Integer userId;// 消息源

    private Integer roomId;// 所在房间

    private Integer receiverId = 0;// 接收者id, 0为房间中的全体成员

    private File file = null;

    private Image image = null;

    private String msg;// 消息内容

    private String fontSize;// 字体大小

    private String fontWeight;// 字体粗细

    private String fontStyle;// 字体风格

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Message() {
        date = simpleDateFormat.format(new Date());
    }

    public Message(String msg, Integer roomId, Integer userId) {
        this();
        this.msg = msg;
        this.roomId = roomId;
        this.userId = userId;
    }

    public Message(String msg, Integer roomId, Integer userId, File file) {
        this(msg, roomId, userId);
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
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
                ", userId=" + userId +
                ", roomId=" + roomId +
                ", file=" + file +
                ", image=" + image +
                ", msg='" + msg + '\'' +
                ", fontSize='" + fontSize + '\'' +
                ", fontWeight='" + fontWeight + '\'' +
                ", fontStyle='" + fontStyle + '\'' +
                '}';
    }
}
