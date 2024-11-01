package com.example.Messenger.exceptions.message;

public class MessageSendingException extends RuntimeException {
    public MessageSendingException(String msg) {
        super(msg);
    }
}
