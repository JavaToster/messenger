package com.example.Messenger.services.message;

import com.example.Messenger.models.message.LinkMessage;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.repositories.chat.ChatRepository;
import com.example.Messenger.repositories.message.LinkMessageRepository;
import com.example.Messenger.repositories.user.MessengerUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LinkMessageService {
    private final LinkMessageRepository linkMessageRepository;
    private final ChatRepository chatRepository;
    private final MessengerUserRepository messengerUserRepository;

    @Autowired
    public LinkMessageService(LinkMessageRepository linkMessageRepository, ChatRepository chatRepository, MessengerUserRepository messengerUserRepository) {
        this.linkMessageRepository = linkMessageRepository;
        this.chatRepository = chatRepository;
        this.messengerUserRepository = messengerUserRepository;
    }

    @Transactional
    public void sendLink(String text, int chatId, int userId, String link) {
        LinkMessage newLink = new LinkMessage(text, chatRepository.findById(chatId).orElse(null), messengerUserRepository.findById(userId).orElse(null), link);
        linkMessageRepository.save(newLink);
    }
}
