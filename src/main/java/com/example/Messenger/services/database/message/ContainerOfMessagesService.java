package com.example.Messenger.services.database.message;

import com.example.Messenger.DAO.message.ContainerOfMessagesDAO;
import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.message.ContainerOfMessages;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.repositories.database.message.ContainerOfMessagesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContainerOfMessagesService {
    private final ContainerOfMessagesDAO containerOfMessagesDAO;

    public ContainerOfMessages getLast(Chat chat){
        return chat.getContainerOfMessages().getLast();
    }

    public ContainerOfMessages getContainerByIdInChat(Chat chat, long idInChat){
        return containerOfMessagesDAO.findById(getIdOfContainerByIdInChat(chat, idInChat));
    }
    public ContainerOfMessages addMessageToContainer(Chat chat, MessageWrapper messageWrapper){
        ContainerOfMessages lastContainer = getLast(chat);
        if(containerOfMessagesDAO.containerIsFull(lastContainer)){
            ContainerOfMessages newContainer = createNewContainer(chat, containerOfMessagesDAO.getIdInChatOfLastContainer(chat)+1);
            newContainer.setMessages(new LinkedList<>(List.of(messageWrapper)));
            messageWrapper.setContainerOfMessages(newContainer);
            return newContainer;
        }

        lastContainer.getMessages().add(messageWrapper);
        messageWrapper.setContainerOfMessages(lastContainer);
        return lastContainer;
    }

    private ContainerOfMessages createNewContainer(Chat chat, long idInChat) {
        return new ContainerOfMessages(idInChat, chat);
    }

    public void save(ContainerOfMessages container) {
        containerOfMessagesDAO.save(container);
    }

    private long getIdOfContainerByIdInChat(Chat chat, long idInChat){
        List<ContainerOfMessages> containers = chat.getContainerOfMessages();
        for(ContainerOfMessages container: containers){
            if(container.equalsById(idInChat)){
                System.out.println(container.getId());
                return container.getId();
            }
        }
        return 0;
    }
}
