package com.example.Messenger.dto.chat;

import lombok.Data;

@Data
public class CreatedChatDTO {
    private long id;

    public CreatedChatDTO(long id){
        this.id = id;
    }
}
