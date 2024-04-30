package com.example.Messenger.services;

import com.example.Messenger.dto.bot.response.message.InfoOfMessageByBotDTO;
import com.example.Messenger.models.Bot;
import com.example.Messenger.models.Chat;
import com.example.Messenger.models.Message;
import com.example.Messenger.repositories.BotRepository;
import com.example.Messenger.util.MessengerMapper;
import com.example.Messenger.util.exceptions.bot.BotNotFoundException;
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

    @Autowired
    public BotRestService(BotRepository botRepository, MessengerMapper mapper, MessageService messageService, ChatMemberService chatMemberService, UserService userService, BotService botService) {
        this.botRepository = botRepository;
        this.mapper = mapper;
        this.messageService = messageService;
        this.chatMemberService = chatMemberService;
        this.userService = userService;
        this.botService = botService;
    }

    public List<InfoOfMessageByBotDTO> getMessages(String token) {
        List<Chat> chats = botService.findChatsByToken(token);
        List<Message> messagesOfUser = new LinkedList<>();
        for(Chat chat: chats){
            messagesOfUser.addAll(botService.getInterlocutorUserMessages(botRepository.findByToken(token).orElse(null), chat));
        }
        return convertToInfoOfMessage(messagesOfUser);
    }

    private List<InfoOfMessageByBotDTO> convertToInfoOfMessage(List<Message> messages){
        List<InfoOfMessageByBotDTO> responseList = new LinkedList<>();

        messages.forEach(message -> responseList.add(mapper.map(message, InfoOfMessageByBotDTO.class)));
        return responseList;
    }

}
