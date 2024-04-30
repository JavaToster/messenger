package com.example.Messenger.dto.chatHead;

public abstract class ChatHeadDTO {
    protected String name;
    protected String underName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnderName() {
        return underName;
    }

    public void setUnderName(String underName) {
        this.underName = underName;
    }
}
