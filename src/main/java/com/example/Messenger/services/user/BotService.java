package com.example.Messenger.services.user;

import com.example.Messenger.models.chat.BotChat;
import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.message.Message;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.models.user.Bot;
import com.example.Messenger.models.user.ChatMember;
import com.example.Messenger.repositories.chat.BotChatRepository;
import com.example.Messenger.repositories.chat.PrivateChatRepository;
import com.example.Messenger.repositories.message.MessageRepository;
import com.example.Messenger.repositories.user.BotRepository;
import com.example.Messenger.repositories.user.ChatMemberRepository;
import com.example.Messenger.util.exceptions.UserNotMemberException;
import com.example.Messenger.util.exceptions.bot.BotNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BotService {
    private final BotRepository botRepository;
    private final MessageRepository messageRepository;
    private final BotChatRepository botChatRepository;
    private final ChatMemberRepository chatMemberRepository;

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

    public List<MessageWrapper> getInterlocutorUserMessages(Bot bot, Chat chat){
        List<MessageWrapper> messages = chat.getMessages();
        List<MessageWrapper> response = new LinkedList<>();
        for(MessageWrapper message: messages){
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
