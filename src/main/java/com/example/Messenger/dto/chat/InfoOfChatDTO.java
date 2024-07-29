package com.example.Messenger.dto.chat;

import com.example.Messenger.dto.message.ContainerOfMessagesDTO;
import com.example.Messenger.dto.message.MessageResponseDTO;
import com.example.Messenger.dto.util.MessagesByDateDTO;
import com.example.Messenger.models.message.ContainerOfMessages;
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
    private ContainerOfMessagesDTO containerOfMessagesDTO;
    private MessagesByDateDTO emptyMessagesByDateDTO;
    private boolean hasMessages;
    private boolean userIsOwner;
//    private List<MessageResponseDTO> messages;
}
