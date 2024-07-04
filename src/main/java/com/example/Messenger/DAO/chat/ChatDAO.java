package com.example.Messenger.DAO.chat;

import com.example.Messenger.DAO.user.MessengerUserDAO;
import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.chat.PrivateChat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatDAO{

    private final MessengerUserDAO messengerUserDAO;

    public String getChatTitle(Chat chat, String username) {
        if (chat.getClass() == PrivateChat.class) {
            return messengerUserDAO.getInterlocutorFromChat(chat, username).getUsername();
        }else{
            return chat.getChatTitleName();
        }
    }
}

