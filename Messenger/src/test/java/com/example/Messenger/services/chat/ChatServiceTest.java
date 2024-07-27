package com.example.Messenger.services.chat;

import com.example.Messenger.repositories.database.chat.ChatRepository;
import com.example.Messenger.repositories.database.user.UserRepository;
import com.example.Messenger.services.database.chat.BotChatService;
import com.example.Messenger.services.database.chat.ChatService;
import com.example.Messenger.services.database.chat.PrivateChatService;
import com.example.Messenger.services.database.user.MessengerUserService;
import com.example.Messenger.services.database.user.UserService;
import com.example.Messenger.util.Convertor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {
    @Mock
    ChatRepository chatRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    UserService userService;
    @Mock
    BotChatService botChatService;
    @Mock
    MessengerUserService messengerUserService;
    @Mock
    PrivateChatService privateChatService;
    @Mock
    Convertor convertor;
    @InjectMocks
    ChatService chatService;

    @Test
    void textInnerOtherText() {
        String fullText = "Hello";
        String word = "He";
        Optional<String> returnedText = chatService.findWordInnerText(fullText, word);
        assertEquals(fullText, returnedText.get());
        assertEquals(Optional.empty(), chatService.findWordInnerText("Hello", "kuk"));
        assertEquals(Optional.empty(), chatService.findWordInnerText("hi", "sdf"));
        assertEquals("Huskashdsjfd", chatService.findWordInnerText("Huskashdsjfd", "kashd").get());
    }
}