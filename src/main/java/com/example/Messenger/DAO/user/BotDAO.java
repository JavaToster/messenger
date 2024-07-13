package com.example.Messenger.DAO.user;

import com.example.Messenger.models.user.Bot;
import com.example.Messenger.repositories.database.user.BotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BotDAO {
    private final BotRepository botRepository;

    public boolean nameIsUnique(String name){
        Optional<Bot> bot = botRepository.findByUsername(name);
        return bot.isEmpty();
    }
}
