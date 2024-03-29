package com.example.bank.repositories;

import com.example.bank.entity.Notification;
import com.example.bank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findByUser(User user);

    List<Notification> findByUserAndIsDelete(User user, boolean isDeleted);

    List<Notification> findByUserAndIsDeleteOrderByDateDispatchDesc(User user, boolean isDelete);

    int countByUser(User user);

    int countByUserAndIsDelete(User user, boolean isDelete);
}
