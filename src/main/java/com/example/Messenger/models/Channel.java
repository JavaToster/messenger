package com.example.Messenger.models;

import jakarta.persistence.*;

@Entity
@Table(name = "Channel")
public class Channel extends Chat{
    @Column(name = "Channel_name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
