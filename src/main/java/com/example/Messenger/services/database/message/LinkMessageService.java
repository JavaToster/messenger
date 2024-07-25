package com.example.Messenger.services.database.message;

import com.example.Messenger.DAO.message.ContainerOfMessagesDAO;
import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.message.ContainerOfMessages;
import com.example.Messenger.models.message.LinkMessage;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.repositories.database.chat.ChatRepository;
import com.example.Messenger.repositories.database.message.ContainerOfMessagesRepository;
import com.example.Messenger.repositories.database.message.LinkMessageRepository;
import com.example.Messenger.repositories.database.user.MessengerUserRepository;
import com.example.Messenger.util.exceptions.ChatNotFoundException;
import com.example.Messenger.util.exceptions.UserNotFoundException;
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
    private final ContainerOfMessagesService containerOfMessagesService;
    private final ContainerOfMessagesRepository containerOfMessagesRepository;

    @Transactional
    public MessageWrapper sendLink(String text, int chatId, int userId, String link) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(ChatNotFoundException::new);

        LinkMessage newLink = new LinkMessage(text, chat, messengerUserRepository.findById(userId).orElseThrow(UserNotFoundException::new), link);
        return newLink;
    }
}
