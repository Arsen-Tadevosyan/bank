package com.example.bank.repositories;

import com.example.bank.entity.ChatRoom;
import com.example.bank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {

    ChatRoom findByUser(User user);

    ChatRoom getByAdmin(User admin);

    ChatRoom findByAdminAndUser(User admin, User user);

    List<ChatRoom> findByAdmin(User user);
}
