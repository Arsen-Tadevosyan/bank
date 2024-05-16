package com.example.bank.service;

import com.example.bank.entity.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatRoomService {

    Optional<String> getChatRoomId(String senderId, String recipientId, boolean createNewRoomIfNotExists);

    String createChatId(String senderId, String recipientId);

    List<ChatRoom> findByRecipientId(String email);
}
