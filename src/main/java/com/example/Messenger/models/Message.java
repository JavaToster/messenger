package com.example.Messenger.models;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "Message")
public class Message {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "message_text")
    private String messageText;
    @Temporal(TemporalType.TIMESTAMP)
    private Date sendingTime;
    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private MessengerUser owner;
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;
    @Enumerated(value = EnumType.STRING)
    private com.example.Messenger.util.enums.Message hasBeenRead;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessageText() {
        return messageText;
    }

    public Message(){}

    public Message(String txt){this.messageText = txt;}


    public Message(String messageText, MessengerUser owner, Chat chat) {
        this.messageText = messageText;
        this.owner = owner;
        this.chat = chat;
        this.sendingTime = new Date();
        this.hasBeenRead = com.example.Messenger.util.enums.Message.NOT_READ;
    }

    public Message setMessageText(String messageText) {
        this.messageText = messageText;
        return this;
    }

    public Date getSendingTime() {
        return sendingTime;
    }

    public void setSendingTime(Date sendingTime) {
        this.sendingTime = sendingTime;
    }

    public MessengerUser getOwner() {
        return owner;
    }

    public void setOwner(MessengerUser owner) {
        this.owner = owner;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public com.example.Messenger.util.enums.Message getHasBeenRead() {
        return hasBeenRead;
    }

    public void setHasBeenRead(com.example.Messenger.util.enums.Message hasBeenRead) {
        this.hasBeenRead = hasBeenRead;
    }
}
