package com.example.Messenger.dto;

import com.example.Messenger.dto.user.FoundUserOfUsername;
import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.redisModel.languageData.LanguageOfApp;
import com.example.Messenger.util.chat.UserFoundedChats;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class MainWindowInfoDTO {
    private LanguageOfApp language;
    private List<ChatDTO> chats;
    private Chat chat;
    private UserFoundedChats foundedChat;
    private UserFoundedChats foundedChat1;
    private List<ChatDTO> foundedChatsOfChatName;
    private List<ChatDTO> foundedChatsOfMessage;
    private List<FoundUserOfUsername> foundUsers;
    private FoundUserOfUsername foundUser;
}
