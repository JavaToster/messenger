package com.example.Messenger.models.message;

import com.example.Messenger.dto.message.MessageResponseDTO;
import com.example.Messenger.models.chat.Chat;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "container_of_messages")
//@Builder
public class ContainerOfMessages implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "id_in_chat")
    private long idInChat;
    @OneToMany(mappedBy = "containerOfMessages")
    private List<MessageWrapper> messages;
    @ManyToOne
    @JoinColumn(name = "chat_id", referencedColumnName = "id")
    private Chat chat;

    public ContainerOfMessages(){}

    public ContainerOfMessages(long idInChat, Chat chat){
        this.idInChat = idInChat;
        this.chat = chat;
    }

    public boolean equalsById(long idInChat){
        return this.idInChat == idInChat;
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof ContainerOfMessages){
            return ((ContainerOfMessages) obj).getId() == this.id;
        }
        return false;
    }
}
