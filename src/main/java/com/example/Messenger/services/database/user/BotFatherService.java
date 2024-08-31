package com.example.Messenger.services.database.user;

import com.example.Messenger.DAO.user.BotDAO;
import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.message.Message;
import com.example.Messenger.models.user.Bot;
import com.example.Messenger.models.user.User;
import com.example.Messenger.repositories.database.message.MessageRepository;
import com.example.Messenger.repositories.database.user.BotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BotFatherService {

    private final BotRepository botRepository;
    private final PasswordEncoder encoder;
    private final BotDAO botDAO;
    private final MessageRepository messageRepository;
    @Value("${bot.father.database.id}")
    private int botFatherId;

    @Transactional
    public void sendMessage(Chat chat, User user, String text){
        Message message = new Message(chat, botRepository.findById(botFatherId).orElse(null));
        if(text.equals("/start")){
            message.setContent("please, send '/create {bot name} 'for get a bot token");
        }else{
            message.setContent(getToken(text));
        }

        messageRepository.save(message);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String getToken(String messageText) {
        String botName = messageText.replace("/create ", "");


        // если после replace остался тот же самый текст(не было команды /create), то значит человек ввел неправильный текст
        if (botName.isEmpty() || botName.equals(messageText)) {
            return "i don't understand you, please enter valid text";
        }

        return createNewBot(botName);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String createNewBot(String name) {
        if(botDAO.nameIsUnique(name)){
            String token = createNewToken(name);
            Bot bot = new Bot(name, token);
            botRepository.save(bot);
            return token;
        }
        return "Bot with username is exists, please enter another name";
    }

    private String createNewToken(String name){
        return encoder.encode(name).replaceAll("/", "");
    }

}
