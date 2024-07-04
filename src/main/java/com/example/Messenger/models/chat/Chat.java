package com.example.Messenger.models.chat;

import com.example.Messenger.models.message.ForwardMessage;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.models.user.ChatMember;
import com.example.Messenger.models.message.BlockMessage;
import jakarta.persistence.*;

import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "Chat")
@Inheritance(strategy = InheritanceType.JOINED)
public class Chat {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;
    @OneToMany(mappedBy = "chat")
    protected List<MessageWrapper> messages;
    @OneToMany(mappedBy = "chat", cascade = CascadeType.REMOVE)
    protected List<ChatMember> members;
    @OneToMany(mappedBy = "chat", cascade = CascadeType.REMOVE)
    protected List<BlockMessage> blockMessages;
    @OneToMany(mappedBy = "fromChat")
    protected List<ForwardMessage> forwardMessages;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<MessageWrapper> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageWrapper> messages) {
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

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", messages=" + messages +
                ", members=" + members +
                ", blockMessages=" + blockMessages +
                ", forwardMessages=" + forwardMessages +
                '}';
    }

    public boolean equals(Chat chat){
        if(this.id == chat.getId()){
            return true;
        }
        return false;
    }

    public String getChatTitleName(){
        return "";
    }

    public String getChatHeader(){
        return "";
    }

    public com.example.Messenger.dto.chat.channel.chatHead.ChatHeadDTO getChatHeadDTO() {
        return null;
    }

    public MessageWrapper getLastMessage(){
        return this.messages.stream().sorted(Comparator.comparingInt(MessageWrapper::getId)).toList().getLast();
    }

    public boolean messagesIsEmpty(){
        return this.messages == null || this.messages.isEmpty();
    }
}
