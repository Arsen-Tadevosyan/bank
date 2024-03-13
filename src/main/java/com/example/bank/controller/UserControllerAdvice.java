package com.example.bank.controller;


import com.example.bank.entity.User;
import com.example.bank.security.SpringUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class UserControllerAdvice {

    @ModelAttribute("currentUser")
    public User currentUser(@AuthenticationPrincipal SpringUser currentUser) {
        if (currentUser != null) {
            return currentUser.getUser();
        }
        return null;
    }
}
