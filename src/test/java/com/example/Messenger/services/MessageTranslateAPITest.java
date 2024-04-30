package com.example.Messenger.services;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class MessageTranslateAPITest {


    @InjectMocks
    private MessageTranslateAPI messageTranslateAPI;

    @Test
    void translate() {
        String text = "hello world";


    }
}