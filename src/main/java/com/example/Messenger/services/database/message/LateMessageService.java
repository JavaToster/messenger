package com.example.Messenger.services.database.message;

import com.example.Messenger.DAO.chat.ChatDAO;
import com.example.Messenger.DAO.message.LateMessageDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LateMessageService {
    private final LateMessageDAO lateMessageDAO;
    private final ChatDAO chatDAO;
    private final BlockMessageService blockMessageService;
    private final LinkMessageService linkMessageService;



}
