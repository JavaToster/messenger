package com.example.Messenger.models.message;

import com.example.Messenger.util.enums.MessageType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;


@Data
@Entity
@Table(name = "Late_message")
public class LateMessage extends MessageWrapper{
    @Enumerated(value = EnumType.ORDINAL)
    private MessageType messageType;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dispatchTime;
    @ManyToOne
    @JoinColumn(name = "container_id", referencedColumnName = "id")
    private LateMessagesContainer container;

    public boolean timeIsExpired(){
        return new Date().getTime() > dispatchTime.getTime();
    }

    public String getMessageTypeAsString(){
        return this.messageType.name();
    }
}
