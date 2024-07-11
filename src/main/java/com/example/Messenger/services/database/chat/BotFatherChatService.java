package com.example.Messenger.services.database.chat;

import com.example.Messenger.dto.chat.InfoOfChatDTO;
import com.example.Messenger.models.chat.BotFatherChat;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.repositories.database.chat.BotFatherChatRepository;
import com.example.Messenger.repositories.database.user.MessengerUserRepository;
import com.example.Messenger.util.exceptions.ChatNotFoundException;
import com.example.Messenger.util.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BotFatherChatService {
    private final BotFatherChatRepository botFatherChatRepository;
    private final MessengerUserRepository messengerUserRepository;

    public BotFatherChat findByUsername(String username){
        return botFatherChatRepository.findByMembers(messengerUserRepository.findByUsername(username).orElseThrow(UserNotFoundException::new)).orElseThrow(ChatNotFoundException::new);
    }
}
