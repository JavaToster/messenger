package com.example.Messenger.dto.bot.response.message;

public class SendMessageForBotDTO {
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
