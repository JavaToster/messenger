package com.example.Messenger.services.user;

import com.example.Messenger.dto.bot.response.message.InfoByTextMessageDTO;
import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.repositories.user.BotRepository;
import com.example.Messenger.services.message.MessageService;
import com.example.Messenger.util.Convertor;
import com.example.Messenger.util.MessengerMapper;
import com.example.Messenger.util.abstractClasses.InfoOfMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class BotRestService {

    private final BotRepository botRepository;
    private final MessengerMapper mapper;
    private final MessageService messageService;
    private final ChatMemberService chatMemberService;
    private final UserService userService;
    private final BotService botService;
    private final Convertor convertor;

    @Autowired
    public BotRestService(BotRepository botRepository, MessengerMapper mapper, MessageService messageService, ChatMemberService chatMemberService, UserService userService, BotService botService, Convertor convertor) {
        this.botRepository = botRepository;
        this.mapper = mapper;
        this.messageService = messageService;
        this.chatMemberService = chatMemberService;
        this.userService = userService;
        this.botService = botService;
        this.convertor = convertor;
    }

    public List<InfoOfMessage> getMessages(String token) {
        List<Chat> chats = botService.findChatsByToken(token);
        List<MessageWrapper> messagesOfUser = new LinkedList<>();
        for(Chat chat: chats){
            messagesOfUser.addAll(botService.getInterlocutorUserMessages(botRepository.findByToken(token).orElse(null), chat));
        }
        System.out.println(convertor.convertToInfoOfMessage(messagesOfUser).getFirst());
        return convertor.convertToInfoOfMessage(messagesOfUser);
    }

}
