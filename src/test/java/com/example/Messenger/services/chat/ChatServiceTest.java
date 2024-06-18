package com.example.Messenger.services.chat;

import com.example.Messenger.repositories.chat.ChatRepository;
import com.example.Messenger.repositories.user.UserRepository;
import com.example.Messenger.services.user.MessengerUserService;
import com.example.Messenger.services.user.UserService;
import com.example.Messenger.util.Convertor;
import jakarta.inject.Inject;
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
        Optional<String> returnedText = chatService.textInnerOtherText(fullText, word);
        assertEquals(fullText, returnedText.get());
        assertEquals(Optional.empty(), chatService.textInnerOtherText("Hello", "kuk"));
        assertEquals(Optional.empty(), chatService.textInnerOtherText("hi", "sdf"));
        assertEquals("Huskashdsjfd", chatService.textInnerOtherText("Huskashdsjfd", "kashd").get());
    }

    @Test
    void textInnerO(){
        String fullText = "Hello";
        String word = "He";
        Optional<String> returnder = chatService.textInnerOtherText1(fullText, word);
        assertEquals(fullText, returnder.get());
    }
}