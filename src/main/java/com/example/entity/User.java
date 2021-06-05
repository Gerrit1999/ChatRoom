package com.example.entity;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    private Integer id;

    private String username;        // 用户名

    private String password;        // 密码

    private String email;           // 邮箱

    private Date recentActiveTime;  // 最后活跃时间

    public User() {
    }

    public User(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public Date getRecentActiveTime() {
        return recentActiveTime;
    }

    public void setRecentActiveTime(Date recentActiveTime) {
        this.recentActiveTime = recentActiveTime;
    }
}