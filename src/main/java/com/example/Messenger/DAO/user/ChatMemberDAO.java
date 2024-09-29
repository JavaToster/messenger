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

    public ChatMember findByChatAndUser(int chatId, int userId){
        return chatMemberRepository.findByUserAndChat(userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user with not found")), chatRepository.findById(chatId).orElseThrow(() -> new ChatNotFoundException("Chat not found"))).orElseThrow(() -> new ChatMemberNotFoundException("user is not member this chat"));
    }

    public ChatMember findByChatAndUser(int chatId, String username){
        return chatMemberRepository.findByUserAndChat(userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("user with id '" +username+"' not found")), chatRepository.findById(chatId).orElseThrow(() -> new ChatNotFoundException("Chat not found"))).orElseThrow(() -> new ChatMemberNotFoundException("user is not member this chat"));
    }

    public ChatMember save(ChatMember chatMember){
        return chatMemberRepository.save(chatMember);
    }

    public List<ChatMember> findByUser(String username) {
        return chatMemberRepository.findByUser(userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("user with username '" +username+"' not found")));
    }

    public boolean userIsMember(String name, List<ChatMember> members) {
        for (ChatMember member: members){
            if(member.getUsernameOfUser().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
