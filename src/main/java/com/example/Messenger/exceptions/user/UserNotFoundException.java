package com.example.Messenger.exceptions.user;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String msg){
        super(msg);
    }
}
