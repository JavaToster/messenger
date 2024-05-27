package com.example.Messenger.services.message;

import com.example.Messenger.dto.util.DateDayOfMessagesDTO;
import com.example.Messenger.models.chat.BotChat;
import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.message.Message;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.repositories.chat.ChatRepository;
import com.example.Messenger.repositories.message.MessageRepository;
import com.example.Messenger.repositories.message.MessageWrapperRepository;
import com.example.Messenger.repositories.message.PhotoMessageRepository;
import com.example.Messenger.services.chat.ChatService;
import com.example.Messenger.services.user.UserService;
import com.example.Messenger.util.Convertor;
import com.example.Messenger.util.MessengerMapper;
import com.example.Messenger.util.enums.MessageStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class MessageWrapperService {
    private final MessageWrapperRepository messageWrapperRepository;
    private final MessageRepository messageRepository;
    private final PhotoMessageRepository photoMessageRepository;
    private final MessageService messageService;
    private final MessengerMapper mapper;
    private final PhotoMessageService photoMessageService;
    private final ChatRepository chatRepository;
    private final ChatService chatService;
    private final UserService userService;
    private final JdbcTemplate jdbcTemplate;
    private final Convertor convertor;
    private final LinkMessageService linkMessageService;

    @Autowired
    public MessageWrapperService(MessageWrapperRepository messageWrapperRepository, MessageRepository messageRepository, PhotoMessageRepository photoMessageRepository, MessageService messageService, MessengerMapper mapper, PhotoMessageService photoMessageService, ChatRepository chatRepository, ChatService chatService, UserService userService, JdbcTemplate jdbcTemplate, Convertor convertor, LinkMessageService linkMessageService) {
        this.messageWrapperRepository = messageWrapperRepository;
        this.messageRepository = messageRepository;
        this.photoMessageRepository = photoMessageRepository;
        this.messageService = messageService;
        this.mapper = mapper;
        this.photoMessageService = photoMessageService;
        this.chatRepository = chatRepository;
        this.chatService = chatService;
        this.userService = userService;
        this.jdbcTemplate = jdbcTemplate;
        this.convertor = convertor;
        this.linkMessageService = linkMessageService;
    }

    @Transactional
    public void sendTextMessage(MessageWrapper message, int chatId, int userId){
        if(!(message.getContent() == null) || message.getContent().isEmpty()) {
            messageService.sendMessage(chatId, userId, message.getContent());
        }
    }

    @Transactional
    public void sendNotImage(String text, int chatId, int userId){
        if(!(text == null) || text.isEmpty()) {
            Optional<String> link = isLink(text);
            if(link.isPresent()){
                linkMessageService.sendLink(text, chatId, userId, link.get());
            }else{
                messageService.sendMessage(chatId, userId, text);
            }
        }
    }

    @Transactional
    public void sendPhoto(MultipartFile photo, int chatId, int userId, String underTextPhoto){
        try {
            photoMessageService.sendMessage(photo, chatId, userId, underTextPhoto);
        }catch (IOException ignored){
            ignored.printStackTrace();
        }
    }

    public List<MessageWrapper> findByChat(int chatId){
        for(MessageWrapper message: messageWrapperRepository.findByChat(chatRepository.findById(chatId).orElse(null))){

        }

        return sortMessagesById(messageWrapperRepository.findByChat(chatRepository.findById(chatId).orElse(null))).reversed();
    }

    @Transactional
    public void messageWasBeRead(int chatId, String username){
        Chat chat = chatRepository.findById(chatId).orElse(null);
        if(chat.getClass() == BotChat.class){
            List<MessageWrapper> messages = chat.getMessages();
            for(MessageWrapper message: messages){
                message.setHasBeenRead(MessageStatus.READ);
            }
            return;
        }
        List<MessageWrapper> messages = chat.getMessages();
        List<MessageWrapper> messagesOfInterlocutor = getMessagesOfInterlocutor(messages, chatService.getInterlocutor(username, chat).getUsername());
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

    private List<Integer> idiesOfMessageWrapper(int chatId){
        return jdbcTemplate.query("select * from Message_wrapper where chat_id = ? order by id desc", new Object[] {chatId}, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("id");
            }
        });
    }

    public static List<MessageWrapper> sortMessagesById(List<MessageWrapper> messages){
        return messages.stream().sorted(Comparator.comparingInt(MessageWrapper::getId)).toList();
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
