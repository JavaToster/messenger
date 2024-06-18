package com.example.Messenger.services.message;

import com.example.Messenger.repositories.message.AnswerMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Deprecated
public class AnswerMessageService {
    private final AnswerMessageRepository answerMessageRepository;

    @Autowired
    public AnswerMessageService(AnswerMessageRepository answerMessageRepository) {
        this.answerMessageRepository = answerMessageRepository;
    }
}
