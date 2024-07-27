package com.example.Messenger.dto.message.rest;

public class ForwardMessageRequestDTO {
    private int toChatId;
    private int forwardMessageId;
    private int ownerId;
    private int fromChatId;

    public int getForwardMessageId() {
        return forwardMessageId;
    }

    public void setForwardMessageId(int forwardMessageId) {
        this.forwardMessageId = forwardMessageId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getToChatId() {
        return toChatId;
    }

    public void setToChatId(int toChatId) {
        this.toChatId = toChatId;
    }

    public int getFromChatId() {
        return fromChatId;
    }

    public void setFromChatId(int fromChatId) {
        this.fromChatId = fromChatId;
    }
}
