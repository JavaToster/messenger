package com.example.Messenger.exceptions.user;

import com.example.Messenger.exceptions.BadRequestException;

public class UserNotFoundException extends BadRequestException {
    public UserNotFoundException(String msg){
        super(msg);
    }

    public UserNotFoundException(){
        super("User not found");
    }
}
