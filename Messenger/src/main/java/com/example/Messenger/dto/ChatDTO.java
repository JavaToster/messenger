package com.example.Messenger.dto;

import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.message.Message;
import com.example.Messenger.models.message.MessageWrapper;

public class ChatDTO {
    private int id;
    private MessageWrapper lastMessage;
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
        if(chat.getMessages().size() == 0){
            Message message = new Message();
            message.setMessageText("No messages here");
            this.lastMessage = message;
            return;
        }
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

    public MessageWrapper getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
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
}
