package com.example.Messenger.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Chat")
@Inheritance(strategy = InheritanceType.JOINED)
public class Chat {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;
    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER)
    protected List<Message> messages;
    @OneToMany(mappedBy = "chat", cascade = CascadeType.REMOVE)
    protected List<ChatMember> members;
    @OneToMany(mappedBy = "chat", cascade = CascadeType.REMOVE)
    protected List<BlockMessage> blockMessages;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<ChatMember> getMembers() {
        return members;
    }

    public void setMembers(List<ChatMember> members) {
        this.members = members;
    }

    public List<BlockMessage> getBlockMessages() {
        return blockMessages;
    }

    public void setBlockMessages(List<BlockMessage> blockMessages) {
        this.blockMessages = blockMessages;
    }
}
