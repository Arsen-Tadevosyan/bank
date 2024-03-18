package com.example.bank.util;

import com.example.bank.entity.Notification;
import com.example.bank.entity.User;
import com.example.bank.entity.enums.NotificationType;
import com.example.bank.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SendNotification {

    private final NotificationService notificationService;

    public Notification sendNotification(User user,
                                         NotificationType notificationType,
                                         String message) {
        return notificationService.save(Notification.builder()
                .notificationType(notificationType)
                .message(message)
                .user(user)
                .dateDispatch(LocalDateTime.now())
                .build());
    }
}
