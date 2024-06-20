package com.example.Messenger.models.database.user;

import jakarta.persistence.*;

@Entity
@Table(name = "Bot")
public class Bot extends MessengerUser{
    @Column(name = "bot_token")
    private String token;

    public Bot(){

    }
    public Bot(String username, String token) {
        this.username = username;
        this.token = token;
    }

    public String getUsername(){
        return this.username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
