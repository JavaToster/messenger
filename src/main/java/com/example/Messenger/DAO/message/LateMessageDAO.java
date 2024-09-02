package com.example.Messenger.DAO.message;

import com.example.Messenger.repositories.database.message.LateMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LateMessageDAO {
    private final LateMessageRepository lateMessageRepository;
}
