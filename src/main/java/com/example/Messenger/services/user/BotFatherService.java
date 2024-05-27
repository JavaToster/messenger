package com.example.Messenger.services.user;

import com.example.Messenger.models.user.Bot;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.repositories.chat.BotChatRepository;
import com.example.Messenger.repositories.chat.PrivateChatRepository;
import com.example.Messenger.repositories.message.MessageRepository;
import com.example.Messenger.repositories.user.BotRepository;
import com.example.Messenger.repositories.user.ChatMemberRepository;
import com.example.Messenger.repositories.user.MessengerUserRepository;
import com.example.Messenger.util.exceptions.bot.BotUsernameIsUsedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BotFatherService {

    private final BotRepository botRepository;
    private final PrivateChatRepository privateChatRepository;
    private final BotChatRepository botChatRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final PasswordEncoder encoder;
    private final MessageRepository messageRepository;
    private final MessengerUserRepository messengerUserRepository;

    @Autowired
    public BotFatherService(BotRepository botRepository, PrivateChatRepository privateChatRepository, BotChatRepository botChatRepository, ChatMemberRepository chatMemberRepository, PasswordEncoder encoder, MessageRepository messageRepository, MessengerUserRepository messengerUserRepository) {
        this.botRepository = botRepository;
        this.privateChatRepository = privateChatRepository;
        this.botChatRepository = botChatRepository;
        this.chatMemberRepository = chatMemberRepository;
        this.encoder = encoder;
        this.messageRepository = messageRepository;
        this.messengerUserRepository = messengerUserRepository;
    }

    @Transactional
    public String getToken(String messageText) {
        String botName = messageText.replace("/create ", "");

        // если после replace остался тот же самый текст(не было команды /create), то значит человек ввел неправильный текст
        if (botName.equals(messageText)) {
            return "i don't understand you, please enter valid text";
        }

        return createNewBot(botName);
    }

    @Transactional
    public String createNewBot(String name) throws BotUsernameIsUsedException {
        nameIsUnique(name);
        String token = createNewToken(name);
        MessengerUser messengerUser = new Bot(name, token);
        messengerUserRepository.save(messengerUser);
        return token;
    }

    private String createNewToken(String name){
        return encoder.encode(name).replaceAll("/", "");
    }

    private void nameIsUnique(String name){
        List<Bot> allBots = botRepository.findAll();
        for(Bot bot: allBots){
            if(bot.getUsername().equals(name)){
                throw new BotUsernameIsUsedException("this username is used, please create another name");
            }
        }

    }

}
