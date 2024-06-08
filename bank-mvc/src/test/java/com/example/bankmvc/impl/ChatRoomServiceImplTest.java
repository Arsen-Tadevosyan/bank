package com.example.bankmvc.impl;

import com.example.bankcommon.entity.ChatRoom;
import com.example.bankcommon.repositories.ChatRoomRepository;
import com.example.bankcommon.service.impl.ChatRoomServiceImpl;
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
class ChatRoomServiceImplTest {

    @InjectMocks
    private ChatRoomServiceImpl chatRoomService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    private final String senderId = "senderId";
    private final String recipientId = "recipientId";

    @BeforeEach
    void setUp() {
    }

    @Test
    void testGetChatRoomId_ExistingRoom() {
        ChatRoom chatRoom = new ChatRoom(1,"chatId", senderId, recipientId);
        when(chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId)).thenReturn(Optional.of(chatRoom));

        Optional<String> chatRoomId = chatRoomService.getChatRoomId(senderId, recipientId, false);

        assertTrue(chatRoomId.isPresent());
        assertEquals("chatId", chatRoomId.get());
    }

    @Test
    void testGetChatRoomId_NewRoom() {
        when(chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId)).thenReturn(Optional.empty());
        when(chatRoomRepository.save(any(ChatRoom.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<String> chatRoomId = chatRoomService.getChatRoomId(senderId, recipientId, true);

        assertTrue(chatRoomId.isPresent());
        assertEquals("senderId_recipientId", chatRoomId.get());
    }


    @Test
    void testFindByRecipientId() {
        ChatRoom chatRoom1 = new ChatRoom(1,"chatId1", senderId, recipientId);
        ChatRoom chatRoom2 = new ChatRoom(2,"chatId2", senderId, recipientId);
        when(chatRoomRepository.findByRecipientId(recipientId)).thenReturn(List.of(chatRoom1, chatRoom2));

        List<ChatRoom> chatRooms = chatRoomService.findByRecipientId(recipientId);

        assertNotNull(chatRooms);
        assertEquals(2, chatRooms.size());
        assertEquals(chatRoom1, chatRooms.get(0));
        assertEquals(chatRoom2, chatRooms.get(1));
    }
}
