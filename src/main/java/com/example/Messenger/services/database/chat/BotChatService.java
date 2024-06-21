package com.example.Messenger.services.database.chat;

import com.example.Messenger.models.database.chat.BotChat;
import com.example.Messenger.models.database.user.Bot;
import com.example.Messenger.models.database.user.ChatMember;
import com.example.Messenger.models.database.user.MessengerUser;
import com.example.Messenger.models.database.user.User;
import com.example.Messenger.repositories.database.chat.BotChatRepository;
import com.example.Messenger.repositories.database.user.ChatMemberRepository;
import com.example.Messenger.util.enums.ChatMemberType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BotChatService {
    private final BotChatRepository botChatRepository;
    private final ChatMemberRepository chatMemberRepository;

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
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
