package com.example.Messenger.DAO.chat;

import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.user.Bot;
import com.example.Messenger.models.user.ChatMember;
import org.springframework.stereotype.Component;

@Component
public class BotChatDAO {

    public boolean isUserInterlocutorIsBotFather(Chat chat){
        for(ChatMember member: chat.getMembers()){
            if(member.getUser().getClass() == Bot.class && member.getUsernameOfUser().equals("Bot father")){
                return true;
            }
        }
        return false;
    }
}
