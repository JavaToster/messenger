package com.example.Messenger.models.message;

import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.util.enums.MessageStatus;
import com.example.Messenger.util.enums.MessageType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "Link_message")
public class LinkMessage extends MessageWrapper implements Serializable {

    // если ссылок будет несколько, то конкатенировать их в одну строку, разделяя их с помощью символа ';'
    // ссылка1;ссылка2;ссылка3
    @Column(name = "link")
    private String link;

    public LinkMessage(){}

    public LinkMessage(String content, Chat chat, MessengerUser owner, String link){
        this.content = content;
        this.chat = chat;
        this.owner = owner;
        this.link = link;
        this.hasBeenRead = MessageStatus.NOT_READ;
        this.sendingTime = new Date();
        this.type = MessageType.LINK;
    }
}
