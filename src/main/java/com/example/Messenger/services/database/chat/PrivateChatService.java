package com.example.Messenger.services.database.chat;

import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.chat.PrivateChat;
import com.example.Messenger.models.user.ChatMember;
import com.example.Messenger.models.user.User;
import com.example.Messenger.repositories.database.chat.PrivateChatRepository;
import com.example.Messenger.repositories.database.user.ChatMemberRepository;
import com.example.Messenger.util.enums.ChatMemberType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PrivateChatService {

    private final PrivateChatRepository privateChatRepository;
    private final ChatMemberRepository chatMemberRepository;
    @Transactional
    public void save(PrivateChat privateChat){
        privateChatRepository.save(privateChat);
    }

    @Transactional
    public int createNewChat(User member1, User member2){
        PrivateChat privateChat = new PrivateChat();
        ChatMember chatMember1 = new ChatMember(member1, privateChat, ChatMemberType.MEMBER);
        ChatMember chatMember2 = new ChatMember(member2, privateChat, ChatMemberType.MEMBER);

        privateChat.setMembers(new ArrayList<>(List.of(chatMember1, chatMember2)));


        privateChatRepository.save(privateChat);
        chatMemberRepository.save(chatMember1);
        chatMemberRepository.save(chatMember2);
        return privateChat.getId();
    }

    public Chat findById(int id) {
        return privateChatRepository.findById(id).orElse(null);
    }
}
