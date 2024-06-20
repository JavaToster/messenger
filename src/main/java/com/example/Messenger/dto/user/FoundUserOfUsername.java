package com.example.Messenger.dto.user;

import com.example.Messenger.models.database.user.User;

public class FoundUserOfUsername {
    private String username;

    public FoundUserOfUsername(User user){
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
