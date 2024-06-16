package com.example.Messenger.util;

import com.example.Messenger.dto.ChatDTO;
import com.example.Messenger.models.chat.Channel;
import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.chat.GroupChat;
import com.example.Messenger.models.chat.PrivateChat;
import com.example.Messenger.models.message.Message;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.models.user.ChatMember;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.models.user.User;
import com.example.Messenger.repositories.chat.BotChatRepository;
import com.example.Messenger.repositories.chat.ChannelRepository;
import com.example.Messenger.repositories.chat.ChatRepository;
import com.example.Messenger.repositories.chat.GroupChatRepository;
import com.example.Messenger.repositories.message.PhotoMessageRepository;
import com.example.Messenger.repositories.user.UserRepository;
import com.example.Messenger.services.message.PhotoMessageService;
import com.example.Messenger.util.enums.ChatMemberType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ConvertorTest {

    @Mock
    GroupChatRepository groupChatRepository;
    @Mock
    ChannelRepository channelRepository;
    @Mock
    BotChatRepository botChatRepository;
    @Mock
    ChatRepository chatRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    PhotoMessageRepository photoMessageRepository;
    @Mock
    PhotoMessageService photoMessageService;
    @InjectMocks
    Convertor convertor;

    @Test
    void testConvertToChatDTOByPrivateChat(){
        PrivateChat chat1 = new PrivateChat();

        MessengerUser messengerUser1 = new User("Kamil", "Gizatullin", "TosterW", "1234", "kamil.gizatullin.03@gmail.com", "79393826388", "RUSSIAN");
        MessengerUser messengerUser2 = new User("Rayan", "Gosling", "RayanGosling", "1111", "kamil.gizatullin.08@mail.ru", "79393826376", "ENGLISH");
        ChatMember chatMember1 = new ChatMember(messengerUser1, chat1, ChatMemberType.MEMBER);
        ChatMember chatMember2 = new ChatMember(messengerUser2, chat1, ChatMemberType.MEMBER);
        MessageWrapper messageWrapper1 = new Message("Hello", messengerUser1, chat1);
        MessageWrapper messageWrapper2 = new Message("Hi", messengerUser2, chat1);

        chat1.setId(188);
        chat1.setMembers(List.of(chatMember1, chatMember2));
        chat1.setMessages(List.of(messageWrapper1, messageWrapper2));

        ChatDTO expectedChatDTO = new ChatDTO(chat1.getId());
        expectedChatDTO.setLastMessageText(messageWrapper2.getContent());
        expectedChatDTO.setChatTitle(messengerUser2.getUsername());
        expectedChatDTO.setLastMessageSendTime(addZero(chat1.getMessages().getLast().getSendingTime().getHours()) + ":" + addZero(chat1.getMessages().getLast().getSendingTime().getMinutes()));
        expectedChatDTO.setBannedChat(false);


        ChatDTO actual = convertor.convertToChatDTO(chat1, messengerUser1.getUsername());
        assertEquals(actual, expectedChatDTO);
    }

    @Test
    void testConvertToChatDTOList(){
        Chat privateChat = new PrivateChat();
        MessengerUser messengerUser1 = new User("Kamil", "Gizatullin", "TosterW", "1234", "kamil.gizatullin.03@gmail.com", "79393826388", "RUSSIAN");
        MessengerUser messengerUser2 = new User("Rayan", "Gosling", "RayanGosling", "1111", "kamil.gizatullin.08@mail.ru", "79393826376", "ENGLISH");
        ChatMember chatMember1 = new ChatMember(messengerUser1, privateChat, ChatMemberType.MEMBER);
        ChatMember chatMember2 = new ChatMember(messengerUser2, privateChat, ChatMemberType.MEMBER);
        MessageWrapper messageWrapper1 = new Message("Hello", messengerUser1, privateChat);
        MessageWrapper messageWrapper2 = new Message("Hi", messengerUser2, privateChat);

        privateChat.setId(189);
        privateChat.setMembers(List.of(chatMember1, chatMember2));

        privateChat.setMessages(List.of(messageWrapper1, messageWrapper2));

        ChatDTO trueChatDTO = new ChatDTO(privateChat.getId());
        trueChatDTO.setLastMessageText(messageWrapper2.getContent());
        trueChatDTO.setChatTitle(messengerUser2.getUsername());
        trueChatDTO.setLastMessageSendTime(addZero(privateChat.getMessages().getLast().getSendingTime().getHours()) + ":" + addZero(privateChat.getMessages().getLast().getSendingTime().getMinutes()));
        trueChatDTO.setBannedChat(false);

        List<Chat> chatList = List.of(privateChat, privateChat);
        List<ChatDTO> trueChatDTOList = List.of(trueChatDTO, trueChatDTO);

        List<ChatDTO> actualChatDTOList = convertor.convertToChatDTO(chatList, messengerUser1.getUsername());
        assertEquals(trueChatDTOList, actualChatDTOList);
    }

    @Test
    @Transactional
    void testChatDTOByGroupChat(){
        GroupChat chat2 = new GroupChat();
        MessengerUser messengerUser1 = new User("Kamil", "Gizatullin", "TosterW", "1234", "kamil.gizatullin.03@gmail.com", "79393826388", "RUSSIAN");
        MessengerUser messengerUser2 = new User("Rayan", "Gosling", "RayanGosling", "1111", "kamil.gizatullin.08@mail.ru", "79393826376", "ENGLISH");

        ChatMember chatMember1 = new ChatMember(messengerUser1, chat2, ChatMemberType.MEMBER);
        ChatMember chatMember2 = new ChatMember(messengerUser2, chat2, ChatMemberType.MEMBER);
        MessageWrapper messageWrapper1 = new Message("Hello", messengerUser1, chat2);
        MessageWrapper messageWrapper2 = new Message("Hi", messengerUser2, chat2);

        chat2.setGroupName("Super_Group");
        chatMember1.setMemberType(ChatMemberType.OWNER);

        chat2.setMembers(List.of(chatMember1, chatMember2));
        chat2.setMessages(List.of(messageWrapper1, messageWrapper2));
        chat2.setId(190);

        ChatDTO trueChatDTO = new ChatDTO(chat2.getId());
        trueChatDTO.setLastMessageText(messageWrapper2.getContent());
        trueChatDTO.setChatTitle(messengerUser2.getUsername());
        trueChatDTO.setLastMessageSendTime(addZero(chat2.getMessages().getLast().getSendingTime().getHours()) + ":" + addZero(chat2.getMessages().getLast().getSendingTime().getMinutes()));
        trueChatDTO.setBannedChat(false);
        trueChatDTO.setId(chat2.getId());
        trueChatDTO.setChatTitle(chat2.getGroupName());

        ChatDTO gotChatDTO = convertor.convertToChatDTO(chat2, messengerUser1.getUsername());

        assertEquals(gotChatDTO, trueChatDTO);
    }

    @Test
    void testChatDTOByChannel(){
        Channel channel = new Channel();
        MessengerUser messengerUser1 = new User("Kamil", "Gizatullin", "TosterW", "1234", "kamil.gizatullin.03@gmail.com", "79393826388", "RUSSIAN");
        MessengerUser messengerUser2 = new User("Rayan", "Gosling", "RayanGosling", "1111", "kamil.gizatullin.08@mail.ru", "79393826376", "ENGLISH");
        ChatMember chatMember1 = new ChatMember(messengerUser1, channel, ChatMemberType.MEMBER);
        ChatMember chatMember2 = new ChatMember(messengerUser2, channel, ChatMemberType.MEMBER);
        MessageWrapper messageWrapper1 = new Message("Hello", messengerUser1, channel);
        MessageWrapper messageWrapper2 = new Message("Hi", messengerUser2, channel);

        channel.setName("Super channel");
        chatMember1.setMemberType(ChatMemberType.OWNER);

        channel.setMembers(List.of(chatMember1, chatMember2));
        channel.setMessages(List.of(messageWrapper1, messageWrapper2));
        channel.setId(190);

        ChatDTO trueChatDTO = new ChatDTO(channel.getId());
        trueChatDTO.setLastMessageText(messageWrapper2.getContent());
        trueChatDTO.setChatTitle(messengerUser2.getUsername());
        trueChatDTO.setLastMessageSendTime(addZero(channel.getMessages().getLast().getSendingTime().getHours()) + ":" + addZero(channel.getMessages().getLast().getSendingTime().getMinutes()));
        trueChatDTO.setBannedChat(false);
        trueChatDTO.setId(channel.getId());
        trueChatDTO.setChatTitle(channel.getName());

        ChatDTO actualChatDTO = convertor.convertToChatDTO(channel, messengerUser1.getUsername());
        assertEquals(actualChatDTO, trueChatDTO);
    }

    private String addZero(int time){
        return time < 10 ? "0"+time : ""+time;
    }
}