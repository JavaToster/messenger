package com.example.Messenger.models.database.user;

import com.example.Messenger.models.database.message.ForwardMessage;
import jakarta.persistence.*;

import java.util.List;

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

    @OneToMany(mappedBy = "fromOwner")
    protected List<ForwardMessage> forwardMessages;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean equals(String username){
        if(this.username.equals(username)){
            return true;
        }
        return false;
    }
}
