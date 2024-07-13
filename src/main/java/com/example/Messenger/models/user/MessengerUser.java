package com.example.Messenger.models.user;

import com.example.Messenger.models.message.ForwardMessage;
import com.example.Messenger.models.message.Message;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "Messenger_user")
@Inheritance(strategy = InheritanceType.JOINED)
public class MessengerUser {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;
    @Column(name = "username")
    protected String username;
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<ChatMember> members;
    @OneToMany(mappedBy = "owner")
    @JsonIgnore
    private List<Message> messages;

    public boolean equals(String username){
        if(this.username.equals(username)){
            return true;
        }
        return false;
    }
}
