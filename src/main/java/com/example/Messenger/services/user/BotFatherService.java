package com.example.Messenger.services.user;

import com.example.Messenger.models.database.user.Bot;
import com.example.Messenger.models.database.user.MessengerUser;
import com.example.Messenger.repositories.user.BotRepository;
import com.example.Messenger.repositories.user.MessengerUserRepository;
import com.example.Messenger.util.exceptions.bot.BotUsernameIsUsedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BotFatherService {

    private final BotRepository botRepository;
    private final PasswordEncoder encoder;
    private final MessengerUserRepository messengerUserRepository;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String getToken(String messageText) {
        String botName = messageText.replace("/create ", "");

        // если после replace остался тот же самый текст(не было команды /create), то значит человек ввел неправильный текст
        if (botName.equals(messageText)) {
            return "i don't understand you, please enter valid text";
        }

        return createNewBot(botName);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
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
