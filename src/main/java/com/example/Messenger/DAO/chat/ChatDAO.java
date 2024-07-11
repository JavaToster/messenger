package com.example.Messenger.DAO.chat;

import com.example.Messenger.DAO.user.MessengerUserDAO;
import com.example.Messenger.models.chat.BotChat;
import com.example.Messenger.models.chat.Channel;
import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.chat.PrivateChat;
import com.example.Messenger.models.user.ChatMember;
import com.example.Messenger.models.user.User;
import com.example.Messenger.repositories.database.chat.ChatRepository;
import com.example.Messenger.repositories.database.user.UserRepository;
import com.example.Messenger.util.enums.ChatMemberType;
import com.example.Messenger.util.exceptions.ChatNotFoundException;
import com.example.Messenger.util.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ChatDAO{

    private final MessengerUserDAO messengerUserDAO;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    public String getChatTitle(Chat chat, String username) {
        if (chat.getClass() == PrivateChat.class) {
            return messengerUserDAO.getInterlocutorFromChat(chat, username).getUsername();
        }else{
            return chat.getChatTitleName();
        }
    }

    public boolean userIsOwner(Chat chat, String username) {
        List<ChatMember> members = chat.getMembers();
        for(ChatMember member: members){
            if(member.getMemberType() == ChatMemberType.OWNER){
                return member.getUsernameOfUser().equals(username);
            }
        }
        return false;
    }

    public List<Chat> sortChatsByLastMessage(String username){
        List<ChatMember> chatMembers = getUser(username).getMembers();
        List<Chat> chats = new LinkedList<>();
        try{
            chatMembers.forEach(chatMember -> chats.add(chatMember.getChat()));

            for(int i = 0; i<chats.size()-1; i++){
                Chat chat = chats.get(i);
                if (isLastMessageSendingTimeByFirstChatMoreLastMessageSendingTimeBySecondChat(chat, chats.get(i+1))) {
                    Collections.swap(chats, i, i + 1);
                }
            }
        }catch (NoSuchElementException ignored ){}
        catch (NullPointerException ignored){}

        return chats.reversed();
    }

    private boolean isLastMessageSendingTimeByFirstChatMoreLastMessageSendingTimeBySecondChat(Chat chat1, Chat chat2){
        return chat1.getLastMessage().getSendingTime().getTime() > chat2.getLastMessage().getSendingTime().getTime();
    }

    private User getUser(String username){
        return this.userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
    }

    public int hasPrivateChat(User member1, User member2){
        List<Chat> chatsOfUser = getChatsByUser(member1);
        for(Chat chat: chatsOfUser){
            List<ChatMember> membersOfSecondUser = member2.getMembers();
            for(ChatMember m: membersOfSecondUser){
                if(m.getChat().equals(chat)){
                    return chat.getId();
                }
            }
        }

        return -1;
    }

    public List<Chat> getChatsByUser(User user){
        List<ChatMember> membersOfUser = user.getMembers();
        List<Chat> chatsByUser = new ArrayList<>();
        membersOfUser.forEach(member -> chatsByUser.add(member.getChat()));
        return chatsByUser;
    }

    public String getReturnedHtmlFile(int chatId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(ChatNotFoundException::new);
        if(chat.getClass() == BotChat.class){
            //в этом случае мы изменяем возвращаемую страницу на botChat.html, ведь тут собеседник будет другого типа
            return "/html/chat/botChat";
        }else if(chat.getClass() == Channel.class){
            //опять же проверка на успешное создание чата, также изменение возвращаемой страницы на showChannel, ведь в канале все чуть по другому
            return "/html/chat/showChannel";
        }else{
            return "/html/chat/showChat";
        }
    }
}

