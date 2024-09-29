package com.example.Messenger.exceptions.chat;

import com.example.Messenger.exceptions.BadRequestException;

public class ChatNotFoundException extends BadRequestException {
    public ChatNotFoundException(String str) {
        super(str);
    }
}
