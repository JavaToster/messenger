package com.example.Messenger.dto.user;

import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.models.user.User;

public class FoundUserOfUsername {
    private String username;

    public FoundUserOfUsername(MessengerUser user){
        this.username = user.getUsername();
    }

    public FoundUserOfUsername(){}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
