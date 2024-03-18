package com.example.bank.controller;

import com.example.bank.entity.Notification;
import com.example.bank.security.CurrentUser;
import com.example.bank.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notification")
    public String notificationPage(@AuthenticationPrincipal CurrentUser user,
                                   ModelMap modelMap) {
        List<Notification> notifications = notificationService.findByUserAndIsDelete(user.getUser(), false);
        modelMap.addAttribute("notifications", notifications);
        return "user/notification";
    }

    @GetMapping("/notification/delete/{id}")
    public String isDelete(@PathVariable("id") int id) {
        Notification byId = notificationService.findById(id);
        if (byId != null) {
            byId.setDelete(true);
            notificationService.save(byId);
        }
        return "redirect:/notification";
    }

}
