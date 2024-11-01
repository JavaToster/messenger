package com.example.Messenger.exceptions;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String str){
        super(str);
    }
}
