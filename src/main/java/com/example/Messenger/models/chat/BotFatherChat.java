package com.example.Messenger.models.chat;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;


@Data
@Entity
@Table(name = "Bot_father_chat")
public class BotFatherChat extends Chat{
    @Override
    public String getChatTitleName() {
        return "Bot father";
    }

    @Override
    public String getChatHeader() {
        return "bot";
    }
}
