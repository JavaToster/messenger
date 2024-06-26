package com.example.Messenger.services.database.message;

import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.message.ForwardMessage;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.models.message.ImageMessage;
import com.example.Messenger.repositories.database.chat.ChatRepository;
import com.example.Messenger.repositories.database.message.ForwardMessageRepository;
import com.example.Messenger.repositories.database.message.PhotoMessageRepository;
import com.example.Messenger.repositories.database.user.MessengerUserRepository;
import com.example.Messenger.services.database.chat.ChatService;
import com.example.Messenger.util.enums.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.Messenger.models.chat.Channel;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ForwardMessageService {

    private final ForwardMessageRepository forwardMessageRepository;
    private final MessageWrapperService messageWrapperService;
    private final ChatService chatService;
    private final PhotoMessageRepository photoMessageRepository;
    private final MessengerUserRepository messengerUserRepository;
    private final ChatRepository chatRepository;

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
