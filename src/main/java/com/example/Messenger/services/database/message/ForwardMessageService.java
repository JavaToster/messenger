package com.example.Messenger.services.database.message;

import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.message.ForwardMessage;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.models.message.ImageMessage;
import com.example.Messenger.repositories.database.chat.ChatRepository;
import com.example.Messenger.repositories.database.message.ForwardMessageRepository;
import com.example.Messenger.repositories.database.message.MessageWrapperRepository;
import com.example.Messenger.repositories.database.user.MessengerUserRepository;
import com.example.Messenger.util.enums.MessageType;
import com.example.Messenger.exceptions.chat.ChatNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.Messenger.models.chat.Channel;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ForwardMessageService {

    private final ForwardMessageRepository forwardMessageRepository;
    private final MessageWrapperRepository messageWrapperRepository;
    private final MessengerUserRepository messengerUserRepository;
    private final ChatRepository chatRepository;

    @Transactional
    public ForwardMessage forward(int forwardMessageId, int toChatId, int ownerId, int fromChatId) {
        MessageWrapper message = messageWrapperRepository.findById(forwardMessageId).orElse(null);
        Chat toChat = chatRepository.findById(toChatId).orElseThrow(ChatNotFoundException::new);
        Chat fromChat = chatRepository.findById(fromChatId).orElseThrow(ChatNotFoundException::new);

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
