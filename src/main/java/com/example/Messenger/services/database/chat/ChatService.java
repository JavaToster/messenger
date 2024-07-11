package com.example.Messenger.services.database.chat;

import com.example.Messenger.DAO.chat.ChatDAO;
import com.example.Messenger.dto.ChatDTO;
import com.example.Messenger.dto.user.FoundUserOfUsername;
import com.example.Messenger.models.chat.*;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.models.user.ChatMember;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.models.user.User;
import com.example.Messenger.repositories.database.chat.ChatRepository;
import com.example.Messenger.repositories.database.user.MessengerUserRepository;
import com.example.Messenger.repositories.database.user.UserRepository;
import com.example.Messenger.services.database.user.UserService;
import com.example.Messenger.util.Convertor;
import com.example.Messenger.util.exceptions.ChatNotFoundException;
import com.example.Messenger.util.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final BotChatService botChatService;
    private final MessengerUserRepository messengerUserRepository;
    private final PrivateChatService privateChatService;
    private final Convertor convertor;
    private final ChatDAO chatDAO;

    public Chat findById(int id) {
        return chatRepository.findById(id).orElseThrow(ChatNotFoundException::new);
    }

    public List<Chat> findByUsername(String username){
        List<ChatMember> chatMembers = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new).getMembers();
        List<Chat> chats = new LinkedList<>();

        chatMembers.forEach(member -> chats.add(member.getChat()));

        return chats;
    }

    public List<Chat> findAll() {
        return chatRepository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteEmptyChats(){
        List<Chat> emptyChats = chatRepository.findAll();
        try{
            emptyChats.removeIf(chat -> !chat.getMessages().isEmpty());
            emptyChats.removeIf(chat -> chat.getClass() == Channel.class);
        }catch (Exception ignored){}
        emptyChats.forEach(chatRepository::delete);
    }



    @Transactional
    public int createPrivateOrBotChat(int userOrBotId, String userUsername) {
        MessengerUser interlocutorIsUserOrBot = messengerUserRepository.findById(userOrBotId).orElseThrow(UserNotFoundException::new);
        User interlocutorIsUser = (User) messengerUserRepository.findByUsername(userUsername).orElseThrow(UserNotFoundException::new);
        if(interlocutorIsUserOrBot instanceof User){
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
        MessengerUser interlocutorIsUserOrBot = messengerUserRepository.findByUsername(usernameOfUserOrBot).orElseThrow(UserNotFoundException::new);
        User interlocutorIsUser = (User) messengerUserRepository.findByUsername(userUsername).orElseThrow(UserNotFoundException::new);
        if (interlocutorIsUserOrBot.getClass() == User.class) {
            int idOfChat = chatDAO.hasPrivateChat((User) interlocutorIsUserOrBot, interlocutorIsUser);
            if (idOfChat == -1)
                idOfChat = privateChatService.createNewChat((User) interlocutorIsUserOrBot, interlocutorIsUser);
            return idOfChat;
        } else {
            return botChatService.createNewChat(interlocutorIsUser, interlocutorIsUserOrBot);
        }
    }

    public List<ChatDTO> findChatsBySearchTextByChatName(String findText, String usernameOfUser) {
        return sortedChatsBySearchedText(convertor.convertToChatDTO(chatDAO.sortChatsByLastMessage(usernameOfUser), usernameOfUser), findText);
    }

    private List<ChatDTO> sortedChatsBySearchedText(List<ChatDTO> chatDTOS, String textForFind) {
        List<ChatDTO> foundChatsByChatNameByTextForFind = new LinkedList<>();

        for(int i = 0; i<chatDTOS.size(); i++){
            ChatDTO chat = chatDTOS.get(i);
            if(findWordInnerText(chat.getChatTitle(), textForFind).isPresent()){
                foundChatsByChatNameByTextForFind.add(chat);
            }
        }

        return foundChatsByChatNameByTextForFind;
    }

    public Optional<String> findWordInnerText(String fullText, String word){
        if(fullText.length() >= word.length()){
            if (checkWordInnerText(fullText, word)) {
                return Optional.of(fullText);
            }
        }
        return Optional.empty();
    }

    private int getFirstFoundCharacterId(String text, char firstCharacterOfText) {
        return text.indexOf(firstCharacterOfText);
    }

    private String cutTextToWord(String text, int indexOfFirstFoundChar, int willEqualTextLength){
        try {
            return text.substring(indexOfFirstFoundChar - 1, indexOfFirstFoundChar + willEqualTextLength - 1);
        }catch (StringIndexOutOfBoundsException e){
            return text.substring(indexOfFirstFoundChar, indexOfFirstFoundChar+willEqualTextLength);
        }
    }

    private boolean checkWordInnerText(String text, String word){
        text = text.toLowerCase();
        if(text.equals(word.toLowerCase())){
            return true;
        }
        char firstCharacterOfText = word.toLowerCase().charAt(0);
        while(text.length()>=word.length()){
            int indexOfFirstFoundChar = getFirstFoundCharacterId(text, firstCharacterOfText);

            if(indexOfFirstFoundChar == -1){
                return false;
            }

            text = text.substring(indexOfFirstFoundChar);

            indexOfFirstFoundChar = getFirstFoundCharacterId(text, firstCharacterOfText);

            if(text.length() < word.length()){
                return false;
            }

            String substringText = cutTextToWord(text, indexOfFirstFoundChar, word.length());

            if(substringText.equals(word.toLowerCase())){
                return true;
            }

            text = text.substring(indexOfFirstFoundChar+word.length());
        }
        return false;
    }

    public List<ChatDTO> findChatsBySearchTextInnerMessages(String text, String username){
        List<Chat> chats = chatDAO.sortChatsByLastMessage(username);
        List<ChatDTO> foundChatsByFoundMessagesInnerChatBySearchText = new LinkedList<>();
        for(Chat chat: chats){
            Optional<ChatDTO> chatDTO = checkTextInMessagesText(chat, text, username);
            if(chatDTO.isEmpty()){
                continue;
            }
            foundChatsByFoundMessagesInnerChatBySearchText.add(chatDTO.get());
        }
        return foundChatsByFoundMessagesInnerChatBySearchText;
    }

    public List<FoundUserOfUsername> findUsersOfUsernameForFindBySearchText(String searchText, String username){
        List<User> users = userService.findWithout(username);
        List<FoundUserOfUsername> foundUserBySearchText = new LinkedList<>();
        for(User user: users){
            if(findWordInnerText(user.getUsername(), searchText).isPresent()){
                foundUserBySearchText.add(new FoundUserOfUsername(user));
            }
        }

        return foundUserBySearchText;
    }

    private Optional<ChatDTO> checkTextInMessagesText(Chat chat, String searchText, String usernameOfUser){
        List<MessageWrapper> messages = chat.getMessages();
        int maxMessagesCheckLimit = 100;
        int count = 0;
        for(MessageWrapper message: messages){
            if(count > maxMessagesCheckLimit){
                return Optional.empty();
            }
            Optional<String> returnedWord = findWordInnerText(message.getContent(), searchText);
            if(returnedWord.isPresent()){
                ChatDTO chatDTO = convertor.convertToChatDTO(chat, usernameOfUser);
                chatDTO.setLastMessageText(returnedWord.get());
                return Optional.of(chatDTO);
            }
            count++;
        }
        return Optional.empty();
    }

    public User getUser(String username){
        return userRepository.findByUsername(username).orElse(null);
    }
}
