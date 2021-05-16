package com.example.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable {
    private Integer id;

    private Date date;

    private Integer roomId;

    private User sender;    // 发送方

    private User receiver;  // 接收方

    private File file;

    private String message;

    private Integer fontSize;

    private Integer fontWeight;

    private String fontStyle;

    public Message() {
        date = new Date();
    }

    public Message(String message, Integer roomId, User sender, User receiver) {
        this();
        this.message = message;
        this.roomId = roomId;
        sender.setPassword(null);
        this.sender = sender;
        if (receiver != null) {
            receiver.setPassword(null);
            this.receiver = receiver;
        }
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

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
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
                ", receiver=" + receiver +
                ", file=" + file +
                ", message='" + message + '\'' +
                ", fontSize=" + fontSize +
                ", fontWeight=" + fontWeight +
                ", fontStyle='" + fontStyle + '\'' +
                '}';
    }
}