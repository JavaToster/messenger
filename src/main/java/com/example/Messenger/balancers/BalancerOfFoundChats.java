package com.example.Messenger.balancers;

import com.example.Messenger.DAO.user.UserDAO;
import com.example.Messenger.dto.chat.ChatDTO;
import com.example.Messenger.dto.user.FoundUserOfUsername;
import com.example.Messenger.models.user.User;
import com.example.Messenger.services.database.user.UserService;
import com.example.Messenger.util.chat.UserFoundedChats;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class BalancerOfFoundChats {
    private static Map<Integer, UserFoundedChats> mapOfUsersChats = new HashMap<>();
    private final UserDAO userDAO;

    public void addNewUser(User user){
        mapOfUsersChats.put(user.getId(), new UserFoundedChats());
    }

    public void addNewUser(String username){
        mapOfUsersChats.put(userDAO.findByUsername(username).getId(), new UserFoundedChats());
    }

    public Map<String, List<ChatDTO>> userFoundedChats(String username){
        List<ChatDTO> chatsOfChatName = mapOfUsersChats.get(userDAO.findByUsername(username).getId()).getChatsOfChatName();
        List<ChatDTO> chatsOfMessages = mapOfUsersChats.get(userDAO.findByUsername(username).getId()).getChatsOfMessagesText();

        return Map.of("ChatName", chatsOfChatName, "MessageText", chatsOfMessages);
    }

    public List<FoundUserOfUsername> foundUsers(String username){
        return mapOfUsersChats.get(userDAO.findByUsername(username).getId()).getUsers();
    }

    public void addFoundedChats(String username, List<ChatDTO> chatsOfChatName, List<ChatDTO> chatsOfMessage, List<FoundUserOfUsername> users){
        UserFoundedChats foundedChats = mapOfUsersChats.get(userDAO.findByUsername(username).getId());
        foundedChats.setChatsOfChatName(chatsOfChatName);
        foundedChats.setChatsOfMessagesText(chatsOfMessage);
        foundedChats.setUsers(users);
        mapOfUsersChats.put(userDAO.findByUsername(username).getId(), foundedChats);
    }

    public boolean checkFoundEmptyChatsOfUser(String username) {
        try {
            List<ChatDTO> chatsByChatName = mapOfUsersChats.get(userDAO.findByUsername(username).getId()).getChatsOfChatName();
            List<ChatDTO> chatsByMessage = mapOfUsersChats.get(userDAO.findByUsername(username).getId()).getChatsOfMessagesText();
            if(chatsByChatName.isEmpty() && chatsByMessage.isEmpty()){
                return true;
            }
            return false;
        }catch (NullPointerException e){
            return true;
        }
    }
}
