package com.example.Messenger.services;

import com.example.Messenger.dto.message.MessageResponseDTO;
import com.example.Messenger.dto.util.DateDayOfMessagesDTO;
import com.example.Messenger.models.*;
import com.example.Messenger.repositories.*;
import com.example.Messenger.util.MessengerMapper;
import com.example.Messenger.util.exceptions.ChatNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final BlockMessageRepository blockMessageRepository;
    private final UserRepository userRepository;
    private final ChatService chatService;
    private final MessengerMapper messengerMapper;
    private final MessageTextEncoder encoder;
    @Autowired
    public MessageService(MessageRepository messageRepository, ChatRepository chatRepository, BlockMessageRepository blockMessageRepository, UserRepository userRepository, ChatService chatService, MessengerMapper messengerMapper, MessageTextEncoder encoder) {
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
        this.blockMessageRepository = blockMessageRepository;
        this.userRepository = userRepository;
        this.chatService = chatService;
        this.messengerMapper = messengerMapper;
        this.encoder = encoder;
    }

    @Transactional
    public void save(Message message){
        messageRepository.save(message);
    }

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

    public List<Message> findBYUser(MessengerUser user){
        return messageRepository.findByOwner(user);
    }

    @Transactional
    public void sendMessage(int chatId, int userId, Message newMessage)  {
        User messageOwner = userRepository.findById(userId).orElse(null);
        Chat chat = chatRepository.findById(chatId).orElse(null);

        messageOwner.setLastOnline(new Date());

        messageRepository.save(new Message(newMessage.getMessageText(), messageOwner, chat));
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

    public List<MessageResponseDTO> convertToMessageDTO(List<Message> messages, String username){
        List<MessageResponseDTO> messageResponseDTOS = new ArrayList<>();

        for(Message message: messages){
            MessageResponseDTO messageResponseDTO = new MessageResponseDTO(message.getMessageText(), (User) message.getOwner(), message.getHasBeenRead());
            messageResponseDTO.setDate(addZeroToTime(message.getSendingTime().getHours())+":"+addZeroToTime(message.getSendingTime().getMinutes()));
            if(message.getOwner().equals(username)){
                messageResponseDTO.setUserIsOwner(true);
            }
            messageResponseDTOS.add(messageResponseDTO);
        }

        return messageResponseDTOS;
    }

    public List<DateDayOfMessagesDTO> convertToMessageDayDTO(List<Message> messages, String username){
        return messengerMapper.convertToMessagesDTO(messages, username);
    }

    private String addZeroToTime(int time){
        return time<10? "0"+time : time+"";
    }

    public boolean checkMessageIsBanned(Message message, int chatId) {
        List<BlockMessage> blockedMessages = blockMessageRepository.findByChat(chatRepository.findById(chatId).orElse(null));
        for(BlockMessage blockMessage: blockedMessages){
            if(blockMessage.getText().equalsIgnoreCase(blockMessage.getText().toLowerCase())){
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void messageWasBeRead(int chatId, String username) {
        Chat chat = chatRepository.findById(chatId).orElse(null);
        if(chatService.findById(chatId).getClass() == BotChat.class){
            List<Message> messages = chat.getMessages();
            for(Message message: messages){
                message.setHasBeenRead(com.example.Messenger.util.enums.Message.READ);
                messageRepository.save(message);
            }
            return;
        }
        List<Message> messages = chatRepository.findById(chatId).orElse(null).getMessages();
        List<Message> messagesOfInterlocutor = getMessagesByUser(messages, chatService.getInterlocutor(username, chatRepository.findById(chatId).orElse(null)).getUsername());
        for(Message message: messagesOfInterlocutor){
            message.setHasBeenRead(com.example.Messenger.util.enums.Message.READ);
            messageRepository.save(message);
        }
    }

    public List<Message> getMessagesByUser(List<Message> messages, String username){
        List<Message> messagesOfUser = new LinkedList<>();

        for(Message message: messages){
            if(message.getOwner().equals(username)){
                messagesOfUser.add(message);
            }
        }
        return messagesOfUser;
    }
}
