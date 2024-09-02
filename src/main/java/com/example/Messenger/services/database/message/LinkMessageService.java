package com.example.Messenger.services.database.message;

import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.message.LinkMessage;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.repositories.database.chat.ChatRepository;
import com.example.Messenger.repositories.database.message.ContainerOfMessagesRepository;
import com.example.Messenger.repositories.database.message.LinkMessageRepository;
import com.example.Messenger.repositories.database.user.MessengerUserRepository;
import com.example.Messenger.exceptions.chat.ChatNotFoundException;
import com.example.Messenger.exceptions.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    public MessageWrapper sendLink(String text, int chatId, String username, String link) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(ChatNotFoundException::new);

        return new LinkMessage(text, chat, messengerUserRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("user with username '"+ username + "' not found")), link);
    }

    public Optional<String> isLink(String text){
        String[] splitWords = text.split(" ");
        String link = "";
        for(String word: splitWords){
            if(word.length() < 9){
                continue;
            }
            if(word.substring(0, 8).equals("https://") || word.substring(0, 8).equals("http://")){
                link = word;
            }
        }
        if(link.isEmpty()){
            return Optional.empty();
        }
        return Optional.of(link);
    }
}
