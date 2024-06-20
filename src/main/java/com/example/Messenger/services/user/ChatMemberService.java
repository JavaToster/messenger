package com.example.Messenger.services.user;

import com.example.Messenger.models.database.user.ChatMember;
import com.example.Messenger.models.database.user.MessengerUser;
import com.example.Messenger.repositories.user.ChatMemberRepository;
import com.example.Messenger.repositories.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.example.Messenger.models.database.chat.Chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatMemberService {
    private final ChatMemberRepository chatMemberRepository;
    private final UserRepository userRepository;

    public static Optional<ChatMember> findByChatAndUser(MessengerUser user, Chat chat){
        List<ChatMember> members = chat.getMembers();
        return members.stream().filter(member -> member.getUser().equals(user.getUsername())).findAny();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
