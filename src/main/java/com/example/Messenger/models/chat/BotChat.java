package com.example.Messenger.models.chat;

import com.example.Messenger.models.user.Bot;
import com.example.Messenger.models.user.ChatMember;
import com.example.Messenger.models.user.MessengerUser;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "Bot_chat")
public class BotChat extends Chat{
    @Override
    public String getChatTitleName() {
        return getBotFromChat(this.members).getUsername();
    }

    private Bot getBotFromChat(List<ChatMember> members){
        for(ChatMember member: members){
            if(member.getUserClass() == Bot.class){
                return (Bot) member.getUser();
            }
        }
        throw new RuntimeException("Bot not found from this chat");
    }

    @Override
    public String getChatHeader() {
        return "bot";
    }

    private MessengerUser getBotFromMembers(){
        for(ChatMember chatMember: this.members){
            if(chatMember.getUserClass() == Bot.class){
                return chatMember.getUser();
            }
        }
        return null;
    }
}
