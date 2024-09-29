package com.example.Messenger.exceptions.user;

import com.example.Messenger.exceptions.BadRequestException;

public class ChatMemberNotFoundException extends BadRequestException {
    public ChatMemberNotFoundException(String msg){
        super(msg);
    }
}
