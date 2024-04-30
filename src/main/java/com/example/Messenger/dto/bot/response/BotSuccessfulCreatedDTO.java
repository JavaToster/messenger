package com.example.Messenger.dto.bot.response;

public class BotSuccessfulCreatedDTO extends BotResponseDTO{
    private String bot_token;

    public BotSuccessfulCreatedDTO(int status, String bot_token) {
        this.status = status;
        this.bot_token = bot_token;
    }

    public String getBot_token() {
        return bot_token;
    }

    public void setBot_token(String bot_token) {
        this.bot_token = bot_token;
    }
}
