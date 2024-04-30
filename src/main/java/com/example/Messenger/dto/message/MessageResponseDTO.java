package com.example.Messenger.dto.message;

import com.example.Messenger.models.MessengerUser;
import com.example.Messenger.util.enums.Message;
import com.example.Messenger.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class MessageResponseDTO {
    private String messageText;
    private String date;
    private MessengerUser owner;
    @JsonIgnore
    private boolean read;
    @JsonIgnore
    private boolean userIsOwner;

    public MessageResponseDTO(String messageText, MessengerUser owner, Message message) {
        this.messageText = messageText;
        this.owner = owner;
        if(message == Message.READ){
            this.read = true;
            return;
        }
        this.read = false;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public MessengerUser getOwner() {
        return owner;
    }

    public void setOwner(MessengerUser owner) {
        this.owner = owner;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isUserIsOwner() {
        return userIsOwner;
    }

    public void setUserIsOwner(boolean userIsOwner) {
        this.userIsOwner = userIsOwner;
    }
}
