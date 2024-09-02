package com.example.Messenger.services.database.message;

import com.example.Messenger.DAO.message.LateMessagesContainerDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LateMessagesContainerService {
    private final LateMessagesContainerDAO lateMessagesContainerDAO;
}
