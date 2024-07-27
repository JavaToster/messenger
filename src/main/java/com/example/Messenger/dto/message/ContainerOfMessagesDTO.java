package com.example.Messenger.dto.message;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.List;

@Data
@RedisHash(timeToLive = 60*60)
public class ContainerOfMessagesDTO implements Serializable {
    @Indexed
    private Long id;
    private List<MessageResponseDTO> messages;

    public ContainerOfMessagesDTO(long id, List<MessageResponseDTO> messages){
        this.id = id;
        this.messages = messages;
    }
}
