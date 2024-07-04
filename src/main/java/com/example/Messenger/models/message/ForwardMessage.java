package com.example.Messenger.models.message;

import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.util.enums.MessageStatus;
import com.example.Messenger.util.enums.MessageType;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "Forward_message")
public class ForwardMessage extends MessageWrapper{
    @ManyToOne
    @JoinColumn(name = "from_chat_id", referencedColumnName = "id")
    private Chat fromChat;
    @ManyToOne
    @JoinColumn(name = "from_owner_id", referencedColumnName = "id")
    private MessengerUser fromOwner;
    @Enumerated(EnumType.STRING)
    private MessageType forwardMessageType;
    @Column(name = "text_under_message")
    private String textUnderMessage;

    public ForwardMessage(){}

    public ForwardMessage(String content, Chat toChat, Chat fromChat, MessengerUser owner, MessengerUser fromOwner){
        this.content = content;
        this.chat = toChat;
        this.fromChat = fromChat;
        this.type = MessageType.FORWARD;
        this.owner = owner;
        this.hasBeenRead = MessageStatus.NOT_READ;
        this.sendingTime = new Date();
        this.fromOwner = fromOwner;
    }

    public Chat getFromChat() {
        return fromChat;
    }

    public void setFromChat(Chat fromChat) {
        this.fromChat = fromChat;
    }

    @Override
    public MessageType getType() {
        return type;
    }

    @Override
    public void setType(MessageType type) {
        this.type = type;
    }

    public String getTextUnderMessage() {
        return textUnderMessage == null ? "" : textUnderMessage;
    }

    public void setTextUnderMessage(String textUnderMessage) {
        this.textUnderMessage = textUnderMessage;
    }

    public String getForwardMessageType() {
        return forwardMessageType.name().toLowerCase();
    }

    public void setForwardMessageType(MessageType forwardMessageType) {
        this.forwardMessageType = forwardMessageType;
    }

    public MessengerUser getFromOwner() {
        return fromOwner;
    }

    public void setFromOwner(MessengerUser fromOwner) {
        this.fromOwner = fromOwner;
    }

    @Override
    public String getMessageSendingTime() {
        if (this.forwardMessageType == MessageType.IMAGE) {
            return "image";
        }
        return this.content;
    }
}
