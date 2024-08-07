package com.example.Messenger.models.message;

import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.util.enums.MessageStatus;
import com.example.Messenger.util.enums.MessageType;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "Message")
public class Message extends MessageWrapper implements Serializable {
    public Message(){}

    public Message(String content, MessengerUser owner, Chat chat) {
        this.content = content;
        this.owner = owner;
        this.chat = chat;
        this.sendingTime = new Date();
        this.hasBeenRead = MessageStatus.NOT_READ;
        this.type = MessageType.TEXT;
    }

    public Message(Chat chat, MessengerUser owner){
        this.chat = chat;
        this.owner = owner;
        this.sendingTime = new Date();
        this.hasBeenRead = MessageStatus.NOT_READ;
        this.type = MessageType.TEXT;
        chat.getMessages().add(this);
        owner.getMessages().add(this);
    }
}
