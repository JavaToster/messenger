package com.example.Messenger.dto.rest.bot.response;

public class ErrorResponseDTO extends BotResponseDTO{
    private String message;

    public ErrorResponseDTO(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStr() {
        return message;
    }

    public void setStr(String str) {
        this.message = str;
    }
}
