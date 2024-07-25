package com.example.Messenger.dto.message;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Data
@RedisHash(timeToLive = 60*60)
public class ContainerOfMessagesDTO {
    private List<MessageResponseDTO> messages;

    public ContainerOfMessagesDTO(List<MessageResponseDTO> messages){
        this.messages = messages;
    }
}
