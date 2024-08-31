package com.example.Messenger.dto.chat;

import com.example.Messenger.dto.user.UserDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SearchedChatsAndUsersDTO {
    private List<ChatDTO> foundChatsByTitle;
    private List<ChatDTO> foundChatsByMessages;
    private List<UserDTO> foundUsers;
}
