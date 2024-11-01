package com.example.Messenger.util;

import com.example.Messenger.DAO.chat.ChatDAO;
import com.example.Messenger.DAO.user.UserDAO;
import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.models.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class Finder {

    private final UserDAO userDAO;
    private final ChatDAO chatDAO;

    private boolean checkWordInnerText(String text, String word){
        return Pattern.compile(word.toLowerCase()).matcher(text.toLowerCase()).find();
    }

    public List<Chat> findChatsByTitleLikeText(String text, String username){
        List<Chat> allChatsOfUser = chatDAO.getChatsByUser(userDAO.findByUsername(username));
        List<Chat> chatsWithNameLikeText = new LinkedList<>();
        for(Chat chat: allChatsOfUser){
            String chatTitle = chatDAO.getChatTitle(chat, username);
            if(checkWordInnerText(chatTitle, text)){
                chatsWithNameLikeText.add(chat);
            }
        }
        return chatsWithNameLikeText;
    }

    public List<Chat> findChatsByMessagesTextLikeText(String text, String username) {
        List<Chat> allChatsOfUser = chatDAO.getChatsByUser(userDAO.findByUsername(username));
        int messagesCheckLimit = 100;

        List<Chat> foundChats = new LinkedList<>();
        for (Chat chat : allChatsOfUser) {
            List<MessageWrapper> messagesOfChat = chat.getMessages();
            if (messagesOfChat.size() < messagesCheckLimit) {
                for (MessageWrapper message : messagesOfChat) {
                    if(checkWordInnerText(message.getContent(), text)){
                        foundChats.add(chat);
                    }
                }
            } else {
                for (int i = 0; i < messagesCheckLimit; i++) {
                    if(checkWordInnerText(messagesOfChat.get(i).getContent(), text)){
                        foundChats.add(chat);
                    }
                }
            }
        }

        return foundChats;
    }

    public List<User> findUsersByUsernameLikeText(String text){
        List<User> allUsers = userDAO.findAll();
        List<User> foundUsers = new ArrayList<>();
        for(User user: allUsers){
            if(checkWordInnerText(user.getUsername(), text)){
                foundUsers.add(user);
            }
        }
        return foundUsers;
    }
}
