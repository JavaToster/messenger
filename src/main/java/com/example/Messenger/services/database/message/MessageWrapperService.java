package com.example.Messenger.services.database.message;

import com.example.Messenger.DAO.chat.ChatDAO;
import com.example.Messenger.DAO.user.MessengerUserDAO;
import com.example.Messenger.models.chat.BotChat;
import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.message.ContainerOfMessages;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.repositories.database.chat.ChatRepository;
import com.example.Messenger.repositories.database.message.MessageWrapperRepository;
import com.example.Messenger.util.enums.MessageStatus;
import com.example.Messenger.util.exceptions.ChatNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MessageWrapperService {
    private final MessageWrapperRepository messageWrapperRepository;
    private final MessageService messageService;
    private final PhotoMessageService photoMessageService;
    private final ChatDAO chatDAO;
    private final JdbcTemplate jdbcTemplate;
    private final LinkMessageService linkMessageService;
    private final MessengerUserDAO messengerUserDAO;
    private final BlockMessageService blockMessageService;
    private final ContainerOfMessagesService containerOfMessagesService;

    @Transactional
    public long send(MultipartFile image, int chatId, int userId, String text){
        Chat chat = chatDAO.findById(chatId);
        Optional<MessageWrapper> willSendMessageOptional;
        if(!image.isEmpty()){
            willSendMessageOptional = sendImage(image, chat, userId, text);
        }else {
            willSendMessageOptional = sendNotImage(text, chatId, userId);
        }

        if(willSendMessageOptional.isPresent()){
            MessageWrapper messageWrapper = willSendMessageOptional.get();
            ContainerOfMessages container = containerOfMessagesService.addMessageToContainer(chat, messageWrapper);
            messageWrapperRepository.save(messageWrapper);
            containerOfMessagesService.save(container);
            return container.getIdInChat();
        }
        return chat.getContainerOfMessages().getLast().getIdInChat();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    public Optional<MessageWrapper> sendNotImage(String text, int chatId, int userId){
        if(blockMessageService.contentIsBlocked(text, chatId)){
           return Optional.empty();
        }

        if(!text.isEmpty()) {
            Optional<String> link = isLink(text);
            if(link.isPresent()){
                return Optional.of(linkMessageService.sendLink(text, chatId, userId, link.get()));
            }else{
                return Optional.of(messageService.sendTextMessage(chatId, userId, text));
            }
        }else{
            return Optional.empty();
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    private Optional<MessageWrapper> sendImage(MultipartFile photo, Chat chat, int userId, String underTextPhoto){
        try {
            return Optional.of(photoMessageService.sendMessage(photo, chat, userId, underTextPhoto));
        }catch (IOException ignored){
            return Optional.empty();
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void messageWasBeRead(int chatId, String username){
        Chat chat = chatDAO.findById(chatId);
        if(chat.getClass() == BotChat.class){
            List<MessageWrapper> messages = chat.getMessages();
            for(MessageWrapper message: messages){
                message.setHasBeenRead(MessageStatus.READ);
            }
            return;
        }
        List<MessageWrapper> messages = chat.getMessages();
        List<MessageWrapper> messagesOfInterlocutor = getMessagesOfInterlocutor(messages, messengerUserDAO.getInterlocutorFromChat(chat, username).getUsername());
        for(MessageWrapper message: messagesOfInterlocutor){
            message.setHasBeenRead(MessageStatus.READ);
            messageWrapperRepository.save(message);
        }
    }

    private List<MessageWrapper> getMessagesOfInterlocutor(List<MessageWrapper> messages, String interlocutorUsername){
        List<MessageWrapper> messagesOfUser = new LinkedList<>();

        for(MessageWrapper message: messages){
            if(message.getOwner().equals(interlocutorUsername)){
                messagesOfUser.add(message);
            }
        }
        return messagesOfUser;
    }

    public MessageWrapper findById(int id) {
        return messageWrapperRepository.findById(id).orElse(null);
    }

    private Optional<String> isLink(String text){
        String[] splitWords = text.split(" ");
        String link = "";
        for(String word: splitWords){
            if(word.length() < 9){
                continue;
            }
            if(word.substring(0, 8).equals("https://") || word.substring(0, 8).equals("http://")){
                link = word;
            }
        }
        if(link.isEmpty()){
            return Optional.empty();
        }
        return Optional.of(link);
    }
}
