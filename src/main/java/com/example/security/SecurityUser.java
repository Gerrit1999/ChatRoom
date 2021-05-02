package com.example.security;

import com.example.entity.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public class SecurityUser extends org.springframework.security.core.userdetails.User {
    private final User originalUser;

    public SecurityUser(User originalUser, List<GrantedAuthority> authorities) {
        super(originalUser.getUsername(), originalUser.getPassword(), authorities);
        this.originalUser = originalUser;
        // 将originalUser的密码擦出
        this.originalUser.setPassword(null);
    }

    public User getOriginalUser() {
        return originalUser;
    }
}
