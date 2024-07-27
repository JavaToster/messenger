package com.example.Messenger.DAO.message;

import com.example.Messenger.dto.message.ContainerOfMessagesDTO;
import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.message.ContainerOfMessages;
import com.example.Messenger.repositories.database.message.ContainerOfMessagesRepository;
import com.example.Messenger.util.exceptions.ContainerOfMessagesNotFoundException;
import com.example.Messenger.util.exceptions.MessagesBoxNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ContainerOfMessagesDAO {

    private final ContainerOfMessagesRepository containerOfMessagesRepository;
    private static final long CONTAINER_MAX_MESSAGES_LIMIT = 5;

    public ContainerOfMessages findByIdInChat(Chat chat, long theCurrentOneId){
        for(ContainerOfMessages box: chat.getContainerOfMessages()){
           if(box.equals(theCurrentOneId)){
               return box;
           }
        }
        throw new MessagesBoxNotFoundException();
    }
    public ContainerOfMessages getNext(Chat chat, long theCurrentOneId){
        return findByIdInChat(chat, ++theCurrentOneId);
    }

    public boolean containerIsFull(ContainerOfMessages lastContainer) {
        return lastContainer.getMessages().size() == CONTAINER_MAX_MESSAGES_LIMIT;
    }

    public long getIdInChatOfLastContainer(Chat chat) {
        return chat.getContainerOfMessages().getLast().getIdInChat();
    }

    public void save(ContainerOfMessages container){
        containerOfMessagesRepository.save(container);
    }

    public ContainerOfMessages findById(long id){
        ContainerOfMessages container = containerOfMessagesRepository.findById(id).orElseThrow(ContainerOfMessagesNotFoundException::new);
        Hibernate.initialize(container.getMessages());
        return container;
    }

    public ContainerOfMessages getIdByIdInChat(Chat chat, Long containerIdInChat) {
        List<ContainerOfMessages> containers = chat.getContainerOfMessages();
        for(ContainerOfMessages container: containers){
            if(container.equalsByIdInChat(containerIdInChat)){
               return container;
            }
        }
        return null;
    }
}
