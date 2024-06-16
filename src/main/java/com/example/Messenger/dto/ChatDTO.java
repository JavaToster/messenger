package com.example.Messenger.dto;

import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.message.Message;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.services.message.MessageWrapperService;
import com.example.Messenger.util.enums.MessageType;

import java.util.List;

public class ChatDTO {
    private int id;
//    private MessageWrapper lastMessage;
    private String lastMessageText;
    private String chatTitle;
    private String lastMessageSendTime;
    private boolean bannedChat;

    public ChatDTO(int id){
        this.id = id;
    }
    public ChatDTO(){}
    public ChatDTO(Chat chat) {
        this.id = chat.getId();
    }

    public ChatDTO(String title){
        this.chatTitle = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChatTitle() {
        return chatTitle;
    }

    public void setChatTitle(String chatTitle) {
        this.chatTitle = chatTitle;
    }

    public String getLastMessageSendTime() {
        return lastMessageSendTime;
    }

    public void setLastMessageSendTime(String lastMessageSendTime) {
        this.lastMessageSendTime = lastMessageSendTime;
    }

    public boolean isBannedChat() {
        return bannedChat;
    }

    public void setBannedChat(boolean bannedChat) {
        this.bannedChat = bannedChat;
    }

    public String getLastMessageText() {
        return lastMessageText;
    }

    public void setLastMessageText(String lastMessageText) {
        this.lastMessageText = lastMessageText;
    }

    @Override
    public String toString() {
        return "ChatDTO{" +
                "id=" + id +
                ", lastMessageText='" + lastMessageText + '\'' +
                ", chatTitle='" + chatTitle + '\'' +
                ", lastMessageSendTime='" + lastMessageSendTime + '\'' +
                ", bannedChat=" + bannedChat +
                '}';
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof ChatDTO){
            ChatDTO chatDTO = (ChatDTO) o;
            return toString().equals(((ChatDTO) o).toString());
        }
        return false;
    }
}
