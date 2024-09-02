package com.example.Messenger.services.database.chat;

import com.example.Messenger.DAO.chat.ChatDAO;
import com.example.Messenger.DAO.user.MessengerUserDAO;
import com.example.Messenger.DAO.user.UserDAO;
import com.example.Messenger.dto.chat.NewChatDTO;
import com.example.Messenger.dto.user.UserDTO;
import com.example.Messenger.models.chat.*;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.models.user.ChatMember;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.models.user.User;
import com.example.Messenger.services.database.user.MessengerUserService;
import com.example.Messenger.util.Convertor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Pattern;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {

    private final ChatDAO chatDAO;
    private final UserDAO userDAO;
    private final MessengerUserService messengerUserService;
    private final BotChatService botChatService;
    private final MessengerUserDAO messengerUserDAO;
    private final PrivateChatService privateChatService;
    private final Convertor convertor;

    public Chat findById(int id) {
        return chatDAO.findById(id);
    }

    public List<Chat> findByUsername(String username){
        List<ChatMember> chatMembers = userDAO.findByUsername(username).getMembers();
        List<Chat> chats = new LinkedList<>();

        chatMembers.forEach(member -> chats.add(member.getChat()));

        return chats;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteEmptyChats(){
        List<Chat> emptyChats = chatDAO.findAll();
        try{
            emptyChats.removeIf(chat -> !chat.getMessages().isEmpty());
            emptyChats.removeIf(chat -> chat.getClass() == Channel.class);
        }catch (Exception ignored){}
        emptyChats.forEach(chatDAO::delete);
    }

    @Transactional
    public int createPrivateOrBotChat(UserDTO interlocutorOfUser, String userUsername) {
        MessengerUser interlocutorIsUserOrBot = messengerUserDAO.findById(interlocutorOfUser.getId());
        User interlocutorIsUser = (User) messengerUserDAO.findByUsername(userUsername);
        if(interlocutorIsUserOrBot.getClass() == User.class){
            int idOfChat = chatDAO.hasPrivateChat((User) interlocutorIsUserOrBot, interlocutorIsUser);
            if(idOfChat == -1)
                idOfChat = privateChatService.createNewChat((User) interlocutorIsUserOrBot, interlocutorIsUser);
            return idOfChat;
        }else{
            return botChatService.createNewChat(interlocutorIsUser, interlocutorIsUserOrBot);
        }
    }

    @Transactional
    public int createPrivateOrBotChat(NewChatDTO chatDTO) {
        MessengerUser interlocutorIsUserOrBot = messengerUserDAO.findByUsername(chatDTO.getInterlocutorUsername());
        User interlocutorIsUser = (User) messengerUserDAO.findByUsername(chatDTO.getUsername());
        if(interlocutorIsUserOrBot.getClass() == User.class){
            int idOfChat = chatDAO.hasPrivateChat((User) interlocutorIsUserOrBot, interlocutorIsUser);
            if(idOfChat == -1)
                idOfChat = privateChatService.createNewChat((User) interlocutorIsUserOrBot, interlocutorIsUser);
            return idOfChat;
        }else{
            return botChatService.createNewChat(interlocutorIsUser, interlocutorIsUserOrBot);
        }
    }

    @Transactional
    public int createPrivateOrBotChat(String usernameOfUserOrBot, String userUsername) {
        MessengerUser interlocutorIsUserOrBot = messengerUserDAO.findByUsername(usernameOfUserOrBot);
        User interlocutorIsUser = (User) messengerUserDAO.findByUsername(userUsername);
        if (interlocutorIsUserOrBot.getClass() == User.class) {
            int idOfChat = chatDAO.hasPrivateChat((User) interlocutorIsUserOrBot, interlocutorIsUser);
            if (idOfChat == -1)
                idOfChat = privateChatService.createNewChat((User) interlocutorIsUserOrBot, interlocutorIsUser);
            return idOfChat;
        } else {
            return botChatService.createNewChat(interlocutorIsUser, interlocutorIsUserOrBot);
        }
    }
}
