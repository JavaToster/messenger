package com.example.Messenger.dto.bot.response.message;

public class InfoOfMessageByBotDTO {
    private int chatId;
    private String text;

    public InfoOfMessageByBotDTO(int chatId, String text) {
        this.chatId = chatId;
        this.text = text;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
