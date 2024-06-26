
package com.example.bankcommon.security;


import com.example.bankcommon.entity.User;
import org.springframework.security.core.authority.AuthorityUtils;

public class CurrentUser extends org.springframework.security.core.userdetails.User {
    private User user;

    public CurrentUser(User user) {
        super(user.getEmail(), user.getPassword(),
                user.isActive(), true, true, true, AuthorityUtils.createAuthorityList(user.getUserRole().name()));
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}