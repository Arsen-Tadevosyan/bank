package com.example.bankcommon.service.impl;

import com.example.bankcommon.entity.ChatMessage;
import com.example.bankcommon.repositories.ChatMessageRepository;
import com.example.bankcommon.service.ChatMessageService;
import com.example.bankcommon.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository repository;
    private final ChatRoomService chatRoomService;

    private static final Logger logger = LoggerFactory.getLogger(ChatMessageServiceImpl.class);

    @Override
    public ChatMessage save(ChatMessage chatMessage) {
        var chatId = chatRoomService
                .getChatRoomId(chatMessage.getSenderId(), chatMessage.getRecipientId(), true)
                .orElseThrow();
        chatMessage.setChatId(chatId);
        repository.save(chatMessage);
        logger.info("Chat message saved: {}", chatMessage);
        return chatMessage;
    }

    @Override
    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        var chatId = chatRoomService.getChatRoomId(senderId, recipientId, false);
        List<ChatMessage> chatMessages = chatId.map(repository::findByChatId).orElse(new ArrayList<>());
        logger.info("Found {} chat messages for senderId: {}, recipientId: {}", chatMessages.size(), senderId, recipientId);
        return chatMessages;
    }
}
