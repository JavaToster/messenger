package com.example.Messenger.services.database.message;

import com.example.Messenger.models.message.LinkMessage;
import com.example.Messenger.repositories.database.chat.ChatRepository;
import com.example.Messenger.repositories.database.message.LinkMessageRepository;
import com.example.Messenger.repositories.database.user.MessengerUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LinkMessageService {
    private final LinkMessageRepository linkMessageRepository;
    private final ChatRepository chatRepository;
    private final MessengerUserRepository messengerUserRepository;

    @Transactional
    public void sendLink(String text, int chatId, int userId, String link) {
        LinkMessage newLink = new LinkMessage(text, chatRepository.findById(chatId).orElse(null), messengerUserRepository.findById(userId).orElse(null), link);
        linkMessageRepository.save(newLink);
    }
}
