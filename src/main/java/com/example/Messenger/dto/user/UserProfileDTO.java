package com.example.Messenger.dto.user;

import com.example.Messenger.models.chat.Chat;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class UserProfileDTO{
    private int id;
    private String username;
    private String lastTime;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private List<String> imagesUrl;
    private String iconUrl;
    private List<Chat> chats;
}
