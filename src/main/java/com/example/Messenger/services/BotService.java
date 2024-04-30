package com.example.Messenger.services;

import com.example.Messenger.dto.bot.response.message.InfoOfMessageByBotDTO;
import com.example.Messenger.models.*;
import com.example.Messenger.repositories.*;
import com.example.Messenger.util.MessengerMapper;
import com.example.Messenger.util.exceptions.UserNotMemberException;
import com.example.Messenger.util.exceptions.bot.BotNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BotService {
    private final BotRepository botRepository;
    private final PasswordEncoder encoder;
    private final PrivateChatRepository privateChatRepository;
    private final MessageRepository messageRepository;
    private final BotChatRepository botChatRepository;
    private final ChatMemberRepository chatMemberRepository;

    @Autowired
    public BotService(BotRepository botRepository, PasswordEncoder encoder, PrivateChatRepository privateChatRepository, MessageRepository messageRepository, BotChatRepository botChatRepository, ChatMemberRepository chatMemberRepository){
        this.botRepository = botRepository;
        this.encoder = encoder;
        this.privateChatRepository = privateChatRepository;
        this.messageRepository = messageRepository;
        this.botChatRepository = botChatRepository;
        this.chatMemberRepository = chatMemberRepository;
    }

    public List<Bot> findAll(){
        return botRepository.findAll();
    }
    public Bot findById(int id){
        return botRepository.findById(id).orElse(null);
    }
    @Transactional
    public void sendMessage(int chatId, int botId, String message){
        Bot bot = botRepository.findById(botId).orElse(null);
        BotChat botChat = botChatRepository.findById(chatId).orElse(null);
        Message messageAnswer = new Message(message, bot, botChat);
        botRepository.save(bot);
        botChatRepository.save(botChat);
        messageRepository.save(messageAnswer);
    }

    @Transactional
    public void sendMessage(int chatId, String botToken, String message){
        Bot bot = botRepository.findByToken(botToken).orElse(null);
        BotChat botChat = botChatRepository.findById(chatId).orElse(null);
        Message messageAnswer = new Message(message, bot, botChat);
        botRepository.save(bot);
        botChatRepository.save(botChat);
        messageRepository.save(messageAnswer);
    }

    public List<Chat> findChatsByToken(String token) {
        Bot bot = botRepository.findByToken(token).orElseThrow(BotNotFoundException::new);

        List<ChatMember> members = chatMemberRepository.findByUser(bot);
        List<Chat> chats = new LinkedList<>();
        for(ChatMember member: members){
            chats.add(member.getChat());
        }
        return chats;
    }

    public Bot findByToken(String token){
        return botRepository.findByToken(token).orElseThrow(BotNotFoundException::new);
    }

    public int getIdByToken(String token){
        return botRepository.findByToken(token).orElseThrow(BotNotFoundException::new).getId();
    }

    public List<Message> getInterlocutorUserMessages(Bot bot, Chat chat){
        List<Message> messages = chat.getMessages();
        List<Message> response = new LinkedList<>();
        for(Message message: messages){
            if(!(message.getOwner().getClass() == Bot.class)){
                response.add(message);
            }
        }
        return response;
    }

    public String getBotName(Chat chat) {
        for(ChatMember chatMember: chat.getMembers()){
            if(chatMember.getUser().getClass() == Bot.class){
                return chatMember.getUser().getUsername();
            }
        }
        throw new UserNotMemberException();
    }
}
