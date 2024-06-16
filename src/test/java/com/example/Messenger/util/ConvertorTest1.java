package com.example.Messenger.util;

import com.example.Messenger.dto.ChatDTO;
import com.example.Messenger.models.chat.Chat;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ConvertorTest1 {

    @Mock
    private GroupChatRepository groupChatRepository;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private BotChatRepository botChatRepository;
    @Mock
    private ChatRepository chatRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PhotoMessageRepository photoMessageRepository;
    @Mock
    private  PhotoMessageService photoMessageService;

    @InjectMocks
    Convertor convertor;

    @Test
    public void testConvertToChatDTO(){
        Chat privateChat = new PrivateChat();
        MessengerUser messengerUser1 = new User("Kamil", "Gizatullin", "TosterW", "1234", "kamil.gizatullin.03@gmail.com", "79393826388", "RUSSIAN");
        MessengerUser messengerUser2 = new User("Rayan", "Gosling", "RayanGosling", "1111", "kamil.gizatullin.08@mail.ru", "79393826376", "ENGLISH");
        ChatMember chatMember1 = new ChatMember(messengerUser1, privateChat, ChatMemberType.MEMBER);
        ChatMember chatMember2 = new ChatMember(messengerUser2, privateChat, ChatMemberType.MEMBER);
        MessageWrapper messageWrapper1 = new Message("Hello", messengerUser1, privateChat);
        MessageWrapper messageWrapper2 = new Message("Hi", messengerUser2, privateChat);

        privateChat.setId(1);
        privateChat.setMembers(List.of(chatMember1, chatMember2));

        privateChat.setMessages(List.of(messageWrapper1, messageWrapper2));

        ChatDTO trueChatDTO = new ChatDTO(privateChat.getId());
        trueChatDTO.setLastMessageText(messageWrapper2.getContent());
        trueChatDTO.setChatTitle(messengerUser2.getUsername());
        trueChatDTO.setLastMessageSendTime(addZero(privateChat.getMessages().getLast().getSendingTime().getHours()) + ":" + addZero(privateChat.getMessages().getLast().getSendingTime().getMinutes()));
        trueChatDTO.setBannedChat(false);

        ChatDTO gotChatDTO = convertor.convertToChatDTO(privateChat, messengerUser1.getUsername());

    }


    private String addZero(int time){
        return time < 10 ? "0"+time : ""+time;
    }
}
