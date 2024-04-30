package com.example.Messenger.services;

import com.example.Messenger.models.ChatMember;
import com.example.Messenger.repositories.ChatMemberRepository;
import com.example.Messenger.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.Messenger.models.Chat;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ChatMemberService {
    private final ChatMemberRepository chatMemberRepository;
    private final UserRepository userRepository;

    @Autowired
    public ChatMemberService(ChatMemberRepository chatMemberRepository, UserRepository userRepository) {
        this.chatMemberRepository = chatMemberRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void save(ChatMember chatMember){
        chatMemberRepository.save(chatMember);
    }

    public List<Chat> findByUsername(String username){
        List<ChatMember> chatsMembers = chatMemberRepository.findByUser(userRepository.findByUsername(username).orElse(null));
        List<Chat> chats = new ArrayList<>();
        chatsMembers.forEach(chatMember -> chats.add(chatMember.getChat()));
        return chats;
    }

    public List<Chat> findById(int userId){
        List<ChatMember> chatsMembers = chatMemberRepository.findByUser(userRepository.findById(userId).orElse(null));
        List<Chat> chats = new ArrayList<>();
        chatsMembers.forEach(chatMember -> chats.add(chatMember.getChat()));
        return chats;
    }
}
