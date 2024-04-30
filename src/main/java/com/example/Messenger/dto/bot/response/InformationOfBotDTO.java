package com.example.Messenger.dto.bot.response;

public class InformationOfBotDTO extends BotResponseDTO{
    private String name;
    private String token;

    public InformationOfBotDTO(int status, String name, String token) {
        this.status = status;
        this.name = name;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
