package com.example.Messenger.models.message;

import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.util.enums.MessageStatus;
import com.example.Messenger.util.enums.MessageType;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "Message_wrapper")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public class MessageWrapper implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;
    @Column(name = "content")
    protected String content;
    @Enumerated(value = EnumType.STRING)
    protected MessageType type;
    @Temporal(TemporalType.TIMESTAMP)
    protected Date sendingTime;
    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    protected MessengerUser owner;
    @ManyToOne
    @JoinColumn(name = "chat_id", referencedColumnName = "id")
    protected Chat chat;

    @ManyToOne
    @JoinColumn(name = "container_of_messages", referencedColumnName = "id")
    protected ContainerOfMessages containerOfMessages;
    @Enumerated(value = EnumType.ORDINAL)
    protected MessageStatus hasBeenRead;
    public String getMessageSendingTime() {
        return addZeroToTime(this.sendingTime.getHours())+":"+addZeroToTime(this.sendingTime.getMinutes());
    }

    private String addZeroToTime(int time){
        return time < 10 ? "0"+time : String.valueOf(time);
    }
}