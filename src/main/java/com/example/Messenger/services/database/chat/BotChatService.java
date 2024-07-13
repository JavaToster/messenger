package com.example.Messenger.services.database.chat;

import com.example.Messenger.DAO.chat.BotChatDAO;
import com.example.Messenger.models.chat.BotChat;
import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.message.Message;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.models.user.Bot;
import com.example.Messenger.models.user.ChatMember;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.models.user.User;
import com.example.Messenger.repositories.database.chat.BotChatRepository;
import com.example.Messenger.repositories.database.chat.ChatRepository;
import com.example.Messenger.repositories.database.message.MessageRepository;
import com.example.Messenger.repositories.database.message.MessageWrapperRepository;
import com.example.Messenger.repositories.database.user.ChatMemberRepository;
import com.example.Messenger.repositories.database.user.MessengerUserRepository;
import com.example.Messenger.repositories.database.user.UserRepository;
import com.example.Messenger.services.database.user.BotFatherService;
import com.example.Messenger.util.enums.ChatMemberType;
import com.example.Messenger.util.enums.MessageType;
import com.example.Messenger.util.exceptions.ChatNotFoundException;
import com.example.Messenger.util.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BotChatService {
    private final BotChatRepository botChatRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final ChatRepository chatRepository;
    private final MessengerUserRepository messengerUserRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final BotChatDAO botChatDAO;
    private final BotFatherService botFatherService;

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public int createNewChat(User user, MessengerUser bot) {
        BotChat botChat = new BotChat();
        ChatMember chatMemberUser = new ChatMember(user, botChat, ChatMemberType.MEMBER);
        ChatMember chatMemberBot = new ChatMember(bot, botChat, ChatMemberType.MEMBER);

        botChat.setMembers(List.of(chatMemberBot, chatMemberUser));

        botChatRepository.save(botChat);
        chatMemberRepository.save(chatMemberUser);
        chatMemberRepository.save(chatMemberBot);

        return botChat.getId();
    }

    @Transactional
    public void sendMessage(int chatId, int userId, String text) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(ChatNotFoundException::new);
        User user = (User) messengerUserRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Message message = new Message(text, user, chat);
        chat.getMessages().add(message);
        user.getMessages().add(message);
        messageRepository.save(message);
        userRepository.save(user);
        chatRepository.save(chat);
        if(botChatDAO.isUserInterlocutorIsBotFather(chat)){
            botFatherService.sendMessage(chat, user, text);
        }
    }
}
