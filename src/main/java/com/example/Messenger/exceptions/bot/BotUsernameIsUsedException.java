package com.example.Messenger.exceptions.bot;

public class BotUsernameIsUsedException extends RuntimeException{
    public BotUsernameIsUsedException(String str){
        super(str);
    }
}
