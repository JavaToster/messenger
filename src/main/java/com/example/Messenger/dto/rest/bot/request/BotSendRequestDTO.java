package com.example.Messenger.dto.rest.bot.request;

public class BotSendRequestDTO {
    private String text;
    private int chatId;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }
}
