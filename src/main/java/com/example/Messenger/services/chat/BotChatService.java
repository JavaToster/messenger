package com.example.Messenger.services.chat;

import com.example.Messenger.models.chat.BotChat;
import com.example.Messenger.models.user.Bot;
import com.example.Messenger.models.user.ChatMember;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.models.user.User;
import com.example.Messenger.repositories.chat.BotChatRepository;
import com.example.Messenger.repositories.chat.ChatRepository;
import com.example.Messenger.repositories.message.MessageRepository;
import com.example.Messenger.repositories.user.BotRepository;
import com.example.Messenger.repositories.user.ChatMemberRepository;
import com.example.Messenger.util.enums.ChatMemberType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
