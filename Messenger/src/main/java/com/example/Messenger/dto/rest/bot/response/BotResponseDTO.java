package com.example.Messenger.dto.rest.bot.response;

public abstract class BotResponseDTO {
    protected int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
