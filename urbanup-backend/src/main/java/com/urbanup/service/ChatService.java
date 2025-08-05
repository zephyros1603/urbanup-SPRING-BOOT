package com.urbanup.service;

import com.urbanup.entity.Chat;
import com.urbanup.entity.Message;
import com.urbanup.repository.ChatRepository;
import com.urbanup.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    @Autowired
    public ChatService(ChatRepository chatRepository, MessageRepository messageRepository) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
    }

    public Chat createChat(Chat chat) {
        return chatRepository.save(chat);
    }

    public List<Message> getMessagesForChat(Long chatId) {
        return messageRepository.findByChatId(chatId);
    }

    public Message sendMessage(Message message) {
        return messageRepository.save(message);
    }

    public void deleteChat(Long chatId) {
        chatRepository.deleteById(chatId);
    }
}