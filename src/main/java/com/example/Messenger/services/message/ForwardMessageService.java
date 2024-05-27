package com.example.Messenger.services.message;

import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.message.ForwardMessage;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.models.message.ImageMessage;
import com.example.Messenger.repositories.chat.ChatRepository;
import com.example.Messenger.repositories.message.ForwardMessageRepository;
import com.example.Messenger.repositories.message.PhotoMessageRepository;
import com.example.Messenger.repositories.user.MessengerUserRepository;
import com.example.Messenger.services.chat.ChatService;
import com.example.Messenger.util.enums.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.Messenger.models.chat.Channel;

import java.awt.*;

@Service
@Transactional(readOnly = true)
public class ForwardMessageService {

    private final ForwardMessageRepository forwardMessageRepository;
    private final MessageWrapperService messageWrapperService;
    private final ChatService chatService;
    private final PhotoMessageRepository photoMessageRepository;
    private final MessengerUserRepository messengerUserRepository;
    private final ChatRepository chatRepository;

    @Autowired
    public ForwardMessageService(ForwardMessageRepository forwardMessageRepository, MessageWrapperService messageWrapperService, ChatService chatService, PhotoMessageRepository photoMessageRepository, MessengerUserRepository messengerUserRepository, ChatRepository chatRepository) {
        this.forwardMessageRepository = forwardMessageRepository;
        this.messageWrapperService = messageWrapperService;
        this.chatService = chatService;
        this.photoMessageRepository = photoMessageRepository;
        this.messengerUserRepository = messengerUserRepository;
        this.chatRepository = chatRepository;
    }

    @Transactional
    public ForwardMessage forward(int forwardMessageId, int toChatId, int ownerId, int fromChatId) {
        MessageWrapper message = messageWrapperService.findById(forwardMessageId);
        Chat toChat = chatService.findById(toChatId);
        Chat fromChat = chatService.findById(fromChatId);

        ForwardMessage forwardMessage = new ForwardMessage(message.getContent(), toChat, fromChat, messengerUserRepository.findById(ownerId).orElse(null), message.getOwner());
        forwardMessage.setTextUnderMessage(message.getClass() == ImageMessage.class ? ((ImageMessage) message).getTextUnderPhoto() : "");
        if(message.getClass() == ImageMessage.class){
            forwardMessage.setForwardMessageType(MessageType.IMAGE);
            forwardMessage.setTextUnderMessage(((ImageMessage) message).getTextUnderPhoto());
        }else{
            forwardMessage.setForwardMessageType(MessageType.TEXT);
        }

        return forwardMessageRepository.save(forwardMessage);
    }

    public boolean isUserIsOwnerOfChannel(int toChatId, int ownerId) {
        return ((Channel) chatRepository.findById(toChatId).orElse(null)).getOwner().equals(messengerUserRepository.findById(ownerId).orElse(null));
    }
}
