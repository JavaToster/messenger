package com.example.Messenger.dto.user;

import com.example.Messenger.models.chat.Chat;
import lombok.Data;

import java.util.List;

@Data
public class UserProfileDTO extends UserDTO{
    private List<Chat> chats;
}
