package com.example.bankmvc.controller;

import com.example.bankcommon.security.CurrentUser;
import com.example.bankcommon.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class NotificationControllerAdvice {

    private final NotificationService notificationService;

    @ModelAttribute("notificationByUser")
    public int currentUser(@AuthenticationPrincipal CurrentUser user) {
        if (user != null) {
            return notificationService.countByUserAndIsDelete(user.getUser(), false);
        }
        return 0;
    }
}
