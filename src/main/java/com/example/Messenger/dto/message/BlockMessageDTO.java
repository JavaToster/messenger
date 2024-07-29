package com.example.Messenger.dto.message;

import com.example.Messenger.models.chat.Chat;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlockMessageDTO {
    private long id;
    private String content;
    private Chat chat;
    private String chatTitle;

}
