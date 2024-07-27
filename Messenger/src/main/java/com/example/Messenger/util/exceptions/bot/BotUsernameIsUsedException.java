package com.example.Messenger.util.exceptions.bot;

public class BotUsernameIsUsedException extends RuntimeException{
    public BotUsernameIsUsedException(String str){
        super(str);
    }
}
