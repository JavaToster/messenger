package com.example.Messenger.services;

import com.example.Messenger.models.*;
import com.example.Messenger.repositories.*;
import com.example.Messenger.util.enums.ChatMemberType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class BotChatService {
    private final BotChatRepository botChatRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final BotRepository botRepository;
    private final ChatMemberRepository chatMemberRepository;

    @Autowired
    public BotChatService(BotChatRepository botChatRepository, ChatRepository chatRepository, MessageRepository messageRepository, BotRepository botRepository, ChatMemberRepository chatMemberRepository) {
        this.botChatRepository = botChatRepository;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.botRepository = botRepository;
        this.chatMemberRepository = chatMemberRepository;
    }

    @Transactional
    public int createNewChat(User byUsername, MessengerUser bot) {
        BotChat botChat = new BotChat();
        ChatMember chatMemberUser = new ChatMember(byUsername, botChat, ChatMemberType.MEMBER);
        ChatMember chatMemberBot = new ChatMember(bot, botChat, ChatMemberType.MEMBER);

        botChat.setMembers(new ArrayList<>(List.of(chatMemberUser, chatMemberBot)));

        botChatRepository.save(botChat);
        chatMemberRepository.save(chatMemberUser);
        chatMemberRepository.save(chatMemberBot);

        return botChat.getId();
    }

    public String getBotName(int chatId) {
        List<ChatMember> members = botChatRepository.findById(chatId).orElse(null).getMembers();
        for(ChatMember member: members){
            if(member.getUser().getClass() == Bot.class){
                return member.getUser().getUsername();
            }
        }
        return "";
    }
}
