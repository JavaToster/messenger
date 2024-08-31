package com.example.Messenger.DAO.user;

import com.example.Messenger.exceptions.chat.ChatNotFoundException;
import com.example.Messenger.exceptions.user.ChatMemberNotFoundException;
import com.example.Messenger.exceptions.user.UserNotFoundException;
import com.example.Messenger.models.user.ChatMember;
import com.example.Messenger.repositories.database.chat.ChatRepository;
import com.example.Messenger.repositories.database.user.ChatMemberRepository;
import com.example.Messenger.repositories.database.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatMemberDAO {
    private final ChatMemberRepository chatMemberRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public ChatMember findByChatAndUser(int userId, int chatId){
        return chatMemberRepository.findByUserAndChat(userRepository.findById(userId).orElseThrow(UserNotFoundException::new), chatRepository.findById(chatId).orElseThrow(ChatNotFoundException::new)).orElseThrow(ChatMemberNotFoundException::new);
    }

    public ChatMember save(ChatMember chatMember){
        return chatMemberRepository.save(chatMember);
    }

    public List<ChatMember> findByUser(String username) {
        return chatMemberRepository.findByUser(userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new));
    }
}
