package com.example.Messenger.services.user;

import com.example.Messenger.models.database.chat.Chat;
import com.example.Messenger.models.database.message.MessageWrapper;
import com.example.Messenger.repositories.user.BotRepository;
import com.example.Messenger.util.Convertor;
import com.example.Messenger.util.abstractClasses.InfoOfMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BotRestService {

    private final BotRepository botRepository;
    private final BotService botService;
    private final Convertor convertor;

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
