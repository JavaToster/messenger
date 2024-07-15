package com.example.Messenger.dto.rest.bot.response;

public class BotSuccessfulCreatedDTO extends BotResponseDTO{
    private String bot_token;

    public BotSuccessfulCreatedDTO(String bot_token) {
        this.status = 200;
        this.bot_token = bot_token;
    }

    public String getBot_token() {
        return bot_token;
    }

    public void setBot_token(String bot_token) {
        this.bot_token = bot_token;
    }
}
