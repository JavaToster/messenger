package com.example.Messenger.services;

import com.example.Messenger.services.database.message.MessageTranslateAPI;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

class MessageStatusTranslateAPITestService {


    @InjectMocks
    private MessageTranslateAPI messageTranslateAPI;

    @Test
    void translate() {
        String text = "hello world";


    }
}