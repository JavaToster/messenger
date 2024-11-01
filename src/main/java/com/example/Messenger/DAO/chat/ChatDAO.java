package com.example.Messenger.DAO.chat;

import com.example.Messenger.DAO.user.MessengerUserDAO;
import com.example.Messenger.models.chat.*;
import com.example.Messenger.models.user.ChatMember;
import com.example.Messenger.models.user.User;
import com.example.Messenger.repositories.database.chat.ChatRepository;
import com.example.Messenger.repositories.database.user.MessengerUserRepository;
import com.example.Messenger.repositories.database.user.UserRepository;
import com.example.Messenger.exceptions.chat.ChatNotFoundException;
import com.example.Messenger.exceptions.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatDAO{

    private final MessengerUserDAO messengerUserDAO;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessengerUserRepository messengerUserRepository;

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
            if(member.isOwner() && member.getUsernameOfUser().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public List<Chat> sortChatsByLastMessage(List<Chat> chats){
        try{
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
        return this.userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("user with username "+ username + " not found"));
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

    public Optional<Chat> findChatByMemberUsername(String username){
        User user = (User) messengerUserRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        List<ChatMember> chatMembers = user.getMembers();
        for(ChatMember member: chatMembers){
            if(member.getChat().getMembers().contains(member)){
                return Optional.of(member.getChat());
            }
        }
        return Optional.empty();
    }

    public Chat findById(int id){
        return chatRepository.findById(id).orElseThrow(() -> new ChatNotFoundException("Chat not found"));
    }

    public List<Chat> findAll(){
        return chatRepository.findAll();
    }

    public void delete(Chat chat){
        chatRepository.delete(chat);
    }

    public List<Chat> findByUser(User user) {
        List<Chat> chats = new ArrayList<>();
        user.getMembers().forEach(member -> chats.add(member.getChat()));
        return chats;
    }
}

