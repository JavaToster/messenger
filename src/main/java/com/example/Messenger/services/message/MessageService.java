package com.example.Messenger.services.message;

import com.example.Messenger.models.database.chat.BotChat;
import com.example.Messenger.models.database.chat.Chat;
import com.example.Messenger.models.database.message.BlockMessage;
import com.example.Messenger.models.database.message.Message;
import com.example.Messenger.models.database.message.MessageWrapper;
import com.example.Messenger.models.database.user.MessengerUser;
import com.example.Messenger.models.database.user.User;
import com.example.Messenger.repositories.chat.ChatRepository;
import com.example.Messenger.repositories.message.BlockMessageRepository;
import com.example.Messenger.repositories.message.MessageRepository;
import com.example.Messenger.repositories.message.MessageWrapperRepository;
import com.example.Messenger.repositories.user.UserRepository;
import com.example.Messenger.services.chat.ChatService;
import com.example.Messenger.util.enums.MessageStatus;
import com.example.Messenger.util.exceptions.ChatNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final BlockMessageRepository blockMessageRepository;
    private final UserRepository userRepository;
    private final ChatService chatService;
    private final MessageWrapperRepository messageWrapperRepository;

    public List<Message> findByChat(int id) throws ChatNotFoundException {
        Chat chat = chatRepository.findById(id).orElseThrow(ChatNotFoundException::new);
        return messageRepository.findByChat(chat).reversed();
    }

    public List<Message> findByChat(Chat chat) {
        return messageRepository.findByChat(chat).reversed();
    }

    public List<Message> findByUser(int userId){
        return messageRepository.findByOwner(userRepository.findById(userId).orElse(null));
    }

    public List<Message> findByUser(MessengerUser user){
        return messageRepository.findByOwner(user);
    }

    @Transactional
    public void sendMessage(int chatId, int userId, Message newMessage)  {
        User messageOwner = userRepository.findById(userId).orElse(null);
        Chat chat = chatRepository.findById(chatId).orElse(null);

        messageOwner.setLastOnline(new Date());

        messageRepository.save(new Message(newMessage.getContent(), messageOwner, chat));
        userRepository.save(messageOwner);
    }

    @Transactional
    public void sendMessage(int chatId, int userId, String message){
        User messageOwner = userRepository.findById(userId).orElse(null);
        Chat chat = chatRepository.findById(chatId).orElse(null);

        messageOwner.setLastOnline(new Date());

        messageRepository.save(new Message(message, messageOwner, chat));
        userRepository.save(messageOwner);
    }

    private String addZeroToTime(int time){
        return time<10? "0"+time : time+"";
    }

    public boolean checkMessageIsBanned(MessageWrapper message, int chatId) {
        List<BlockMessage> blockedMessages = blockMessageRepository.findByChat(chatRepository.findById(chatId).orElse(null));
        for(BlockMessage blockMessage: blockedMessages){
            if(blockMessage.getText().equalsIgnoreCase(message.getContent().toLowerCase())){
                return true;
            }
        }
        return false;
    }

    public boolean checkMessageIsBanned(String text, int chatId){
        List<BlockMessage> blockedMessages = blockMessageRepository.findByChat(chatRepository.findById(chatId).orElse(null));
        for(BlockMessage blockMessage: blockedMessages){
            if(blockMessage.getText().equalsIgnoreCase(text.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void messageWasBeRead(int chatId, String username) {
        Chat chat = chatRepository.findById(chatId).orElse(null);
        if(chat.getClass() == BotChat.class){
            List<MessageWrapper> messages = chat.getMessages();
            for(MessageWrapper message: messages){
                message.setHasBeenRead(MessageStatus.READ);
                messageWrapperRepository.save(message);
            }
            return;
        }
        List<MessageWrapper> messages = chatRepository.findById(chatId).orElse(null).getMessages();
        List<MessageWrapper> messagesOfInterlocutor = getMessagesByUser(messages, chatService.getInterlocutor(username, chatRepository.findById(chatId).orElse(null)).getUsername());
        for(MessageWrapper message: messagesOfInterlocutor){
            message.setHasBeenRead(MessageStatus.READ);
            messageWrapperRepository.save(message);
        }
    }

    public List<MessageWrapper> getMessagesByUser(List<MessageWrapper> messages, String username){
        List<MessageWrapper> messagesOfUser = new LinkedList<>();

        for(MessageWrapper message: messages){
            if(message.getOwner().equals(username)){
                messagesOfUser.add(message);
            }
        }
        return messagesOfUser;
    }
}
