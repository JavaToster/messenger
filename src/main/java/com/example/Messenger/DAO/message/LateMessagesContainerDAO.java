package com.example.Messenger.DAO.message;

import com.example.Messenger.repositories.database.message.LateMessagesContainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LateMessagesContainerDAO {
    private final LateMessagesContainerRepository lateMessagesContainerRepository;

}
