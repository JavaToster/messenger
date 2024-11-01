package com.example.Messenger.dto;

import lombok.Data;

@Data
public class ExceptionMessageDTO {
    private String message;

    public ExceptionMessageDTO(String msg){
        this.message = msg;
    }
}
