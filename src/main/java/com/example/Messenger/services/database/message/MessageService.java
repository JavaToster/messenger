package com.example.Messenger.services.database.message;

import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.message.BlockMessage;
import com.example.Messenger.models.message.Message;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.models.user.User;
import com.example.Messenger.repositories.database.chat.ChatRepository;
import com.example.Messenger.repositories.database.message.BlockMessageRepository;
import com.example.Messenger.repositories.database.message.MessageRepository;
import com.example.Messenger.repositories.database.message.MessageWrapperRepository;
import com.example.Messenger.repositories.database.user.UserRepository;
import com.example.Messenger.services.database.chat.ChatService;
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

        messageOwner.setLastOnlineTime(new Date());

        messageRepository.save(new Message(newMessage.getContent(), messageOwner, chat));
        userRepository.save(messageOwner);
    }

    @Transactional
    public void sendMessage(int chatId, int userId, String message){
        User messageOwner = userRepository.findById(userId).orElse(null);
        Chat chat = chatRepository.findById(chatId).orElse(null);

        messageOwner.setLastOnlineTime(new Date());

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
