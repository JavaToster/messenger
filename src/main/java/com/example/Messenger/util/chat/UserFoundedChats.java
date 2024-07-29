package com.example.Messenger.util.chat;

import com.example.Messenger.dto.chat.ChatDTO;
import com.example.Messenger.dto.user.FoundUserOfUsername;
import com.example.Messenger.models.user.User;

import java.util.Collections;
import java.util.List;

public class UserFoundedChats {
    private User owner;
    private List<ChatDTO> chatsOfChatName;
    private List<ChatDTO> chatsOfMessagesText;
    private List<FoundUserOfUsername> users;

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<ChatDTO> getChatsOfChatName() {
        return chatsOfChatName == null ? Collections.emptyList() : chatsOfChatName;
    }

    public void setChatsOfChatName(List<ChatDTO> chatsOfChatName) {
        this.chatsOfChatName = chatsOfChatName;
    }

    public boolean isEmptyChatsOfChatName(){
        if(chatsOfChatName == null){
            return true;
        }
        return chatsOfChatName.isEmpty();
    }

    public List<ChatDTO> getChatsOfMessagesText() {
        return chatsOfMessagesText == null ? Collections.emptyList() : chatsOfMessagesText;
    }

    public void setChatsOfMessagesText(List<ChatDTO> chatsOfMessagesText) {
        this.chatsOfMessagesText = chatsOfMessagesText;
    }

    public boolean isEmptyChatsOfMessages(){
        if(chatsOfMessagesText == null){
            return true;
        }
        return chatsOfMessagesText.isEmpty();
    }

    public List<FoundUserOfUsername> getUsers() {
        return users;
    }

    public void setUsers(List<FoundUserOfUsername> users) {
        this.users = users;
    }
}
