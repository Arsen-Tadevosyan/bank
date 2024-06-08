package com.example.bankmvc.impl;

import com.example.bankcommon.entity.ChatMessage;
import com.example.bankcommon.repositories.ChatMessageRepository;
import com.example.bankcommon.service.ChatRoomService;
import com.example.bankcommon.service.impl.ChatMessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceImplTest {

    @InjectMocks
    private ChatMessageServiceImpl chatMessageService;

    @Mock
    private ChatMessageRepository repository;

    @Mock
    private ChatRoomService chatRoomService;

    private ChatMessage chatMessage;

    @BeforeEach
    void setUp() {
        chatMessage = new ChatMessage();
        chatMessage.setId(1);
        chatMessage.setSenderId("senderId");
        chatMessage.setRecipientId("recipientId");
        chatMessage.setContent("Test message");
    }

    @Test
    void testSaveChatMessage() {

        when(chatRoomService.getChatRoomId(chatMessage.getSenderId(), chatMessage.getRecipientId(), true))
                .thenReturn(Optional.of("chatId"));
        when(repository.save(chatMessage)).thenReturn(chatMessage);

        ChatMessage savedMessage = chatMessageService.save(chatMessage);


        assertNotNull(savedMessage);
        assertEquals(chatMessage, savedMessage);
        assertEquals("chatId", savedMessage.getChatId());
    }

    @Test
    void testFindChatMessages() {
        when(chatRoomService.getChatRoomId(chatMessage.getSenderId(), chatMessage.getRecipientId(), false))
                .thenReturn(Optional.of("chatId"));
        when(repository.findByChatId("chatId")).thenReturn(List.of(chatMessage));

        List<ChatMessage> chatMessages = chatMessageService.findChatMessages(chatMessage.getSenderId(), chatMessage.getRecipientId());

        assertNotNull(chatMessages);
        assertFalse(chatMessages.isEmpty());
        assertEquals(1, chatMessages.size());
        assertEquals(chatMessage, chatMessages.get(0));
    }
}
