package com.example.bank.repositories;

import com.example.bank.entity.ChatRoom;
import com.example.bank.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    List<Message> findByChatroom(ChatRoom chatRoom);

}