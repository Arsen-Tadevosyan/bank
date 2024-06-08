package com.example.bankcommon.repositories;

import com.example.bankcommon.entity.Notification;
import com.example.bankcommon.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findByUser(User user);

    List<Notification> findByUserAndIsDeleteOrderByDateDispatchDesc(User user, boolean isDelete);

    int countByUser(User user);

    int countByUserAndIsDelete(User user, boolean isDelete);
}
