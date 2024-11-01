package com.example.Messenger.services.redis.message;

import com.example.Messenger.dto.message.ContainerOfMessagesDTO;
import com.example.Messenger.dto.message.MessageWrapperDTO;
import com.example.Messenger.models.message.ContainerOfMessages;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContainerOfMessagesCacheManager {

    @CachePut(cacheNames = "containerOfMessage", key = "#container.id")
    public ContainerOfMessagesDTO save(ContainerOfMessagesDTO container){
        return container;
    }

    @Cacheable(cacheNames = "containerOfMessage", key = "#container.id")
    public ContainerOfMessagesDTO getContainer(ContainerOfMessages container, List<MessageWrapperDTO> messages){
        return new ContainerOfMessagesDTO(container.getId(), container.getIdInChat(), messages);
    }
}
