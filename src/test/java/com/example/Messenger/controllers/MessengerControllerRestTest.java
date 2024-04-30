package com.example.Messenger.controllers;

import com.example.Messenger.models.Chat;
import com.example.Messenger.models.User;
import com.example.Messenger.services.*;
import com.example.Messenger.util.threads.DeleteEmptyChats;
import com.example.Messenger.util.MessengerMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@DisplayName("controller test")
class MessengerControllerRestTest {
    @Mock
    private PrivateChatService privateChatService;
    @Mock
    private GroupChatService groupChatService;
    @Mock
    private UserService userService;
    @Mock
    private MessageService messageService;
    @Mock
    private ChatService chatService;
    @Mock
    private ChannelService channelService;
    @Mock
    private BlockMessageService blockMessageService;
    @Mock
    private DeleteEmptyChats deleteEmptyChats;
    @Mock
    private MessengerMapper messengerMapper;

    @InjectMocks
    private MessengerController messengerController;

    @Test
    public void createChannelTest(){
        Chat chat = new Chat();

        chat.setId(1);
        String response1 = messengerController.createChat(chat);
        chat.setId(2);
        String response2 = messengerController.createChat(chat);
        chat.setId(3);
        String response3 = messengerController.createChat(chat);
        chat.setId(4);
        String response4 = messengerController.createChat(chat);

        assertEquals("redirect:/messenger/chats/create", response1);
        assertEquals("redirect:/messenger/chats/create?type=group", response2);
        assertEquals("redirect:/messenger/chats/create?type=channel", response3);
        assertEquals("redirect:/messenger/chats/create", response4);
    }

    @Test
    public void createChatTest(){
        Model model = new ConcurrentModel();
        String type = null;
        String username = "Toster";

        doReturn(List.of(new User())).when(this.userService).findWithout(username);

        String result = messengerController.createChat(model, type, username);

        type="group";
        Model model2 = new ConcurrentModel();
        String result2 = messengerController.createChat(model2, type, username);
        type="channel";
        Model model3 = new ConcurrentModel();
        String result3 = messengerController.createChat(model3, type, username);
        type="dfksdjfd";
        Model model4 = new ConcurrentModel();
        String result4 = messengerController.createChat(model4, type, username);

        assertEquals("/html/chat/createPrivateChat", result);
        assertEquals("/html/chat/createGroupChat", result2);
        assertEquals("/html/chat/createChannel", result3);
        assertEquals("/html/chat/createPrivateChat",result4);
        assertEquals("Toster", model.getAttribute("username"));
        assertEquals(List.of(new User()), model2.getAttribute("users"));
        assertEquals(List.of(new User()), model3.getAttribute("users"));
        assertEquals(List.of(new User()), model4.getAttribute("users"));
    }
}