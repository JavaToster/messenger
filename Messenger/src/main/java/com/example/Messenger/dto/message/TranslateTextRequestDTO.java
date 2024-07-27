package com.example.Messenger.dto.message;

public class TranslateTextRequestDTO {
    private int userId;
    private String text;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
