package com.example.Messenger.dto.chat;

import com.example.Messenger.dto.ChatDTO;
import com.example.Messenger.dto.util.MessagesByDateDTO;
import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.chat.PrivateChat;
import com.example.Messenger.models.user.User;
import lombok.Data;

import java.util.List;

@Data
public class InfoOfChatDTO {
    private int chatId;
    private String interlocutorOrGroupOrChannelName;
    private User user;
    private String lastOnlineTimeOrMembersCount;
    private List<ChatDTO> willForwardChats;
    private ChatDTO emptyForwardChat;
    private boolean privateChat;
    private List<MessagesByDateDTO> messagesByDateDTO;
    private MessagesByDateDTO emptyMessagesByDateDTO;
    private boolean hasMessages;

    public InfoOfChatDTO(Chat chat, User user, String interlocutorOrGroupOrChannelName, String lastOnlineTimeOrMembersCount,
                         List<ChatDTO> willForwardChats, List<MessagesByDateDTO> messagesByDateDTO) {
        this.chatId = chat.getId();
        this.interlocutorOrGroupOrChannelName = interlocutorOrGroupOrChannelName;
        this.user = user;
        this.lastOnlineTimeOrMembersCount = lastOnlineTimeOrMembersCount;
        this.willForwardChats = willForwardChats;
        this.privateChat = chat.getClass() == PrivateChat.class;
        this.messagesByDateDTO = messagesByDateDTO.reversed();
        this.hasMessages = chat.getMessages().isEmpty();

    }
}
