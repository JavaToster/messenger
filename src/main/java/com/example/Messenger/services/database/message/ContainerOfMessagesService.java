package com.example.Messenger.services.database.message;

import com.example.Messenger.DAO.chat.ChatDAO;
import com.example.Messenger.DAO.message.ContainerOfMessagesDAO;
import com.example.Messenger.dto.message.ContainerOfMessagesDTO;
import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.message.ContainerOfMessages;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.services.redis.message.ContainerOfMessagesCachingService;
import com.example.Messenger.util.Convertor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContainerOfMessagesService {
    private final ContainerOfMessagesDAO containerOfMessagesDAO;
    private final Convertor convertor;
    private final ContainerOfMessagesCachingService containerOfMessagesCachingService;
    private final ChatDAO chatDAO;

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
            updateContainerOfMessages(convertor.convertToContainerOfMessagesDTO(newContainer, messageWrapper.getOwner().getUsername()));
            return newContainer;
        }

        lastContainer.getMessages().add(messageWrapper);
        messageWrapper.setContainerOfMessages(lastContainer);
        System.out.println(lastContainer.getMessages().size());
        updateContainerOfMessages(convertor.convertToContainerOfMessagesDTOWithoutCaching(lastContainer, messageWrapper.getOwner().getUsername()));
        return lastContainer;
    }

    private ContainerOfMessages createNewContainer(Chat chat, long idInChat) {
        return new ContainerOfMessages(idInChat, chat);
    }

    private long getIdOfContainerByIdInChat(Chat chat, long idInChat){
        List<ContainerOfMessages> containers = chat.getContainerOfMessages();
        for(ContainerOfMessages container: containers){
            if(container.equalsByIdInChat(idInChat)){
                System.out.println(container.getId());
                return container.getId();
            }
        }
        return 0;
    }

    private ContainerOfMessagesDTO updateContainerOfMessages(ContainerOfMessagesDTO container){
        return containerOfMessagesCachingService.save(container);
    }

    public long getNextContainerId(int chatId, Long idInChat) {
        Chat chat = chatDAO.findById(chatId);

        if(idInChat == 0){
            return containerOfMessagesDAO.getIdInChatOfLastContainer(chat);
        }

        if(!isContainerIsLast(chat, idInChat)){
            return ++idInChat;
        }else{
            return idInChat;
        }
    }

    private boolean isContainerIsLast(Chat chat, long currentOneIdInChat){
        return chat.getContainerOfMessages().getFirst().getIdInChat() <= currentOneIdInChat;
    }

    public long getPreviousContainerId(int chatId, Long currentOneIdInChat){
        Chat chat = chatDAO.findById(chatId);

        System.out.println(currentOneIdInChat);

        if(currentOneIdInChat == 0){
            long lastContainerId = containerOfMessagesDAO.getIdInChatOfLastContainer(chat);
            if(chat.getContainerOfMessages().size() == 1){
                return lastContainerId;
            }else{
                return lastContainerId - 1;
            }
        }

        if(!isContainerIsFirst(chat, currentOneIdInChat)){
            return --currentOneIdInChat;
        }else{
            return currentOneIdInChat;
        }
    }

    private boolean isContainerIsFirst(Chat chat, long currentOneIdInChat) {
        System.out.println(chat.getContainerOfMessages().getLast().getIdInChat());
        return chat.getContainerOfMessages().getLast().getIdInChat() == currentOneIdInChat;
    }
}
