package com.example.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable {
    private Integer id;

    private Date date;

    private Integer roomId;

    private User sender;// 发送方

    private Integer receiverId;// 接收方id

    private File file;

    private String message;

    private Integer fontSize;

    private Integer fontWeight;

    private String fontStyle;

    public Message() {
        receiverId = 0;  // 默认群发
        date = new Date();
    }

    public Message(String message, Integer roomId, User user) {
        this();
        this.message = message;
        this.roomId = roomId;
        user.setPassword(null);
        this.sender = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDate() {
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message == null ? null : message.trim();
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }

    public Integer getFontWeight() {
        return fontWeight;
    }

    public void setFontWeight(Integer fontWeight) {
        this.fontWeight = fontWeight;
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle == null ? null : fontStyle.trim();
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", date=" + date +
                ", roomId=" + roomId +
                ", sender=" + sender +
                ", receiverId=" + receiverId +
                ", file=" + file +
                ", message='" + message + '\'' +
                ", fontSize=" + fontSize +
                ", fontWeight=" + fontWeight +
                ", fontStyle='" + fontStyle + '\'' +
                '}';
    }
}