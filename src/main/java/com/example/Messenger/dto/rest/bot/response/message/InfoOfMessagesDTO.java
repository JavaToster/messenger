package com.example.Messenger.dto.rest.bot.response.message;

import com.example.Messenger.util.abstractClasses.InfoOfMessage;

import java.util.List;

public class InfoOfMessagesDTO {
    private List<InfoOfMessage> messages;

    public InfoOfMessagesDTO(List<InfoOfMessage> messages) {
        this.messages = messages;
    }

    public List<InfoOfMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<InfoOfMessage> messages) {
        this.messages = messages;
    }
}
