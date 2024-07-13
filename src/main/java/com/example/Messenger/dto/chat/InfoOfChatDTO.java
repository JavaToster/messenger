package com.example.Messenger.dto.chat;

import com.example.Messenger.dto.util.MessagesByDateDTO;
import com.example.Messenger.models.user.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class InfoOfChatDTO {
    private int chatId;
    private String interlocutorOrGroupOrChannelName;
    private User user;
    private String lastOnlineTimeOrMembersCount;
    private List<ChatDTO> willForwardChats;
    private ChatDTO emptyForwardChat;
    private String chatType;
    private List<MessagesByDateDTO> messagesByDateDTO;
    private MessagesByDateDTO emptyMessagesByDateDTO;
    private boolean hasMessages;
    private boolean userIsOwner;
}
