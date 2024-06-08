package com.example.bankcommon.service.impl;

import com.example.bankcommon.entity.Notification;
import com.example.bankcommon.entity.User;
import com.example.bankcommon.repositories.NotificationRepository;
import com.example.bankcommon.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;


    @Override
    public List<Notification> findByUser(User user) {
        return notificationRepository.findByUser(user);
    }

    @Override
    public Notification findById(int id) {
        return notificationRepository.findById(id).orElse(null);
    }

    @Override
    public int countByUser(User user) {
        return notificationRepository.countByUser(user);
    }

    @Override
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> findByUserAndIsDelete(User user, boolean isDelete) {
        return notificationRepository.findByUserAndIsDeleteOrderByDateDispatchDesc(user, isDelete);
    }

    @Override
    public int countByUserAndIsDelete(User user, boolean isDelete) {
        return notificationRepository.countByUserAndIsDelete(user, isDelete);
    }
}
