package com.example.bank.service.impl;

import com.example.bank.entity.ChatRoom;
import com.example.bank.entity.Message;
import com.example.bank.repositories.MessageRepository;
import com.example.bank.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    @Override
    public List<Message> getByChatRoom(ChatRoom chatRoom) {
        return messageRepository.findByChatroom(chatRoom);
    }

    @Override
    public Message save(Message message) {
        return messageRepository.save(message);
    }
}
