package com.example.Messenger.dto.rest.bot.response.message;

import com.example.Messenger.util.abstractClasses.InfoOfMessage;

public class InfoByTextMessageDTO extends InfoOfMessage {
    private String text;

    public InfoByTextMessageDTO(int chatId, String text) {
        this.chatId = chatId;
        this.text = text;
        this.type = "text";
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
