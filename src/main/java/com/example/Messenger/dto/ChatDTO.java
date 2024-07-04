package com.example.Messenger.dto;

import com.example.Messenger.models.chat.Channel;
import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.chat.PrivateChat;
import com.example.Messenger.models.message.MessageWrapper;
import lombok.Data;

@Data
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
        if(chat.messagesIsEmpty()){
            if (chat.getClass() == Channel.class){
                this.lastMessageText = "nothing here";
                this.chatTitle = chat.getChatTitleName();
            }
            return;
        }
        MessageWrapper lastMessageByChat = chat.getLastMessage();
        this.lastMessageText = lastMessageByChat.getMessageSendingTime();
        this.lastMessageSendTime = lastMessageByChat.getMessageContent();
    }
}
