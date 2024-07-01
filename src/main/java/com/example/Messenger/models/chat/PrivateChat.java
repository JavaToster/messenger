package com.example.Messenger.models.chat;

import com.example.Messenger.models.user.ChatMember;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.models.user.User;
import jakarta.persistence.*;

import java.util.Optional;

@Entity
@Table(name = "Private_chat")
public class PrivateChat extends Chat {
    public Optional<MessengerUser> getInterlocutor(String username) {
        for (ChatMember member : this.members) {
            if (!member.getUsernameOfUser().equals(username)) {
                return Optional.of(member.getUser());
            }
        }
        return Optional.empty();
    }


}