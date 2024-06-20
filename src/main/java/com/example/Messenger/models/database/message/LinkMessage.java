package com.example.Messenger.models.database.message;

import com.example.Messenger.models.database.chat.Chat;
import com.example.Messenger.models.database.user.MessengerUser;
import com.example.Messenger.util.enums.MessageStatus;
import com.example.Messenger.util.enums.MessageType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Date;

@Entity
@Table(name = "Link_message")
public class LinkMessage extends MessageWrapper{

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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void addLink(String link){
        if(this.link == null || this.link.isEmpty()){
            this.link = link;
        }else{
            this.link += ";"+link;
        }
    }
}
