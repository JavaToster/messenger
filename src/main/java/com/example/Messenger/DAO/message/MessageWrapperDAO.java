package com.example.Messenger.DAO.message;

import com.example.Messenger.dto.message.messageSpecifications.ForwardMessageSpecification;
import com.example.Messenger.dto.message.messageSpecifications.ImageMessageSpecification;
import com.example.Messenger.dto.message.messageSpecifications.LinkMessageSpecification;
import com.example.Messenger.models.message.ForwardMessage;
import com.example.Messenger.models.message.ImageMessage;
import com.example.Messenger.models.message.LinkMessage;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.repositories.database.message.MessageWrapperRepository;
import com.example.Messenger.util.abstractClasses.MessageSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageWrapperDAO {

    private final MessageWrapperRepository messageWrapperRepository;

    public MessageSpecification getSpecificationOfMessage(MessageWrapper message){
        if(message.getClass() == ImageMessage.class){
            return new ImageMessageSpecification(((ImageMessage) message).getTextUnderPhoto());
        }else if(message.getClass() == ForwardMessage.class){
            ForwardMessage forwardMessage = (ForwardMessage) message;
            return new ForwardMessageSpecification(forwardMessage.getForwardMessageType(), forwardMessage.getFromOwner().getUsername(), forwardMessage.getTextUnderMessage());
        }else if(message.getClass() == LinkMessage.class){
            return new LinkMessageSpecification(((LinkMessage) message).getLink());
        }else{
            return MessageSpecification.emptySpecification();
        }
    }

    public boolean userIsMessageOwner(MessageWrapper message, String username) {
        return message.getOwner().equals(username);
    }

    public List<MessageWrapper> sortMessagesById(List<MessageWrapper> messages){

        return new ArrayList<>(messages.stream().sorted(Comparator.comparingInt(MessageWrapper::getId)).toList());
    }

    public void save(MessageWrapper messageWrapper) {
        messageWrapperRepository.save(messageWrapper);
    }
}
