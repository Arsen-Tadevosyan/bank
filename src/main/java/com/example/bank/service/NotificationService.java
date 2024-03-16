package com.example.bank.service;

import com.example.bank.entity.Notification;
import com.example.bank.entity.User;

import java.util.List;

public interface NotificationService {

    List<Notification> findByUser(User user);
    Notification findById(int id);

    int countByUser(User user);

    Notification save(Notification notification);

    List<Notification> findByUserAndIsDelete(User user, boolean isDelete);

    int countByUserAndIsDelete(User user,boolean isDelete);
}
