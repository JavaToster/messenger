package com.example.Messenger.services.database.chat;

import com.example.Messenger.dto.ChatDTO;
import com.example.Messenger.dto.user.FoundUserOfUsername;
import com.example.Messenger.models.database.chat.Channel;
import com.example.Messenger.models.database.chat.Chat;
import com.example.Messenger.models.database.chat.GroupChat;
import com.example.Messenger.models.redis.LanguageOfApp;
import com.example.Messenger.models.database.message.MessageWrapper;
import com.example.Messenger.models.database.user.ChatMember;
import com.example.Messenger.models.database.user.MessengerUser;
import com.example.Messenger.models.database.user.User;
import com.example.Messenger.repositories.database.chat.ChatRepository;
import com.example.Messenger.repositories.database.user.UserRepository;
import com.example.Messenger.services.database.user.MessengerUserService;
import com.example.Messenger.services.database.user.UserService;
import com.example.Messenger.util.Convertor;
import com.example.Messenger.util.exceptions.ChatNotFoundException;
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
    private final MessengerUserService messengerUserService;
    private final PrivateChatService privateChatService;
    private final Convertor convertor;
    public Chat findById(int id) {
        return chatRepository.findById(id). orElseThrow(ChatNotFoundException::new);
    }

    public List<Chat> findByMemberUsername(String username) {
        List<Chat> chats = new ArrayList<>();
        userRepository.findByUsername(username).orElse(null).getMembers().forEach(chatMember -> chats.add(chatMember.getChat()));
        return chats;
    }

    public List<Chat> findByUsername(String username){
        List<ChatMember> chatMembers = userRepository.findByUsername(username).orElse(null).getMembers();
        List<Chat> chats = new ArrayList<>();

        for(ChatMember chatMember: chatMembers){
            chats.add(chatMember.getChat());
        }
        return chats;
    }

    public MessengerUser getInterlocutor(String username, Chat chat){
        List<ChatMember> members = chat.getMembers();
        for(ChatMember chatMember: members){
            MessengerUser member = chatMember.getUser();
            if(member.getUsername().equals(username)){
                continue;
            }
            return member;
        }
        return null;
    }

    public List<Chat> sortChatsByLastMessage(String username){
        List<ChatMember> chatMembers = userRepository.findByUsername(username).orElse(null).getMembers();
        List<Chat> chats = new ArrayList<>();
        try{
            chatMembers.forEach(chatMember -> chats.add(chatMember.getChat()));

            for(int i = 0; i<chats.size()-1; i++){
                Chat chat = chats.get(i);
                if (chat.getMessages().getLast().getSendingTime().getTime() > chats.get(i + 1).getMessages().getLast().getSendingTime().getTime()) {
                    Collections.swap(chats, i, i + 1);
                }
        }
        }catch (NoSuchElementException ignored ){}
        catch (NullPointerException ignored){}

        return chats.reversed();
    }

    private String addZero(int time){
        return time < 10 ? "0"+time : ""+time;
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

    public String countMembers(int chatId, LanguageOfApp language) {
        Chat chat = chatRepository.findById(chatId).orElse(null);
        int membersCount = chat.getMembers().size();
        if (chat.getClass() == GroupChat.class) {
            return language.getType().equals("ru") ? membersCount <5 ? membersCount + " участника" : membersCount + " участников" : membersCount + " " + language.getGroupMembers();
        } else {
            return language.getType().equals("ru") ? membersCount<5 ? membersCount + " подписчика" : membersCount + " подписчиков" : membersCount + " " + language.getGroupMembers();
        }
    }



    @Transactional
    public int createPrivateOrBotChat(MessengerUser user, String username) {
        MessengerUser messengerUser = messengerUserService.findById(user.getId());
        if(messengerUser.getClass() == User.class){
//            User user1 = userService.findById(user.getId());
            User user1 = (User) user;
            int id = isPrivateChat(user1, userService.findByUsername(username));
            if(id == -1)
                id = privateChatService.createNewChat(userService.findById(user.getId()), userService.findByUsername(username));
            return id;
        }else{
            return botChatService.createNewChat(userService.findByUsername(username), user);
        }
    }

    public List<ChatDTO> findChatsBySearchTextByChatName(String findText, String usernameOfUser) {
        return sortedChatsBySearchedText(convertor.convertToChatDTO(sortChatsByLastMessage(usernameOfUser), usernameOfUser), findText);
    }

    private List<ChatDTO> sortedChatsBySearchedText(List<ChatDTO> chatDTOS, String findText) {

        List<ChatDTO> willReturnChats = new LinkedList<>();

        for(int i = 0; i<chatDTOS.size(); i++){
            ChatDTO chat = chatDTOS.get(i);
            if(findWordInnerText(chat.getChatTitle(), findText).isPresent()){
                willReturnChats.add(chat);
            }
        }

        return willReturnChats;
    }

    /** метод для определения того, есть ли какое либо слово внутри текста*/
    /** возвращает Optional <полный текст> в случае обнаружения слова в тексте*/
    /** возвращает Optional.empty() если слова нет в тексте*/
    public Optional<String> findWordInnerText(String fullText, String word){
        if(fullText.length() >= word.length()){
            if (fullText.toLowerCase().equals(word.toLowerCase()) || checkWordInnerText(fullText, word)) {
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
        char firstCharacterOfText = word.toLowerCase().charAt(0);
        while(text.length()>=word.length()){
            int indexOfFirstFoundChar = getFirstFoundCharacterId(text, firstCharacterOfText);

            /**если возвращает -1 значит такого символа нет в тексте -> этого слова тоже нет в тексте*/
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

    public List<ChatDTO> findChatsBySearchTextByMessages(String text, String username){
        List<Chat> chats = sortChatsByLastMessage(username);
        List<ChatDTO> willReturnedChats = new LinkedList<>();
        for(Chat chat: chats){
            Optional<ChatDTO> chatDTO = textInMessageText(chat, text, username);
            if(chatDTO.isEmpty()){
                continue;
            }
            willReturnedChats.add(chatDTO.get());
        }
        return willReturnedChats;
    }

    public List<FoundUserOfUsername> findUsersOfUsername(String text, String username){
        List<User> users = userService.findWithout(username);
        List<FoundUserOfUsername> willReturnList = new LinkedList<>();
        for(User user: users){
            if(findWordInnerText(user.getUsername(), text).isPresent()){
                willReturnList.add(new FoundUserOfUsername(user));
            }
        }

        System.out.println(willReturnList);

        return willReturnList;
    }

    private Optional<ChatDTO> textInMessageText(Chat chat, String text, String usernameOfUser){
        List<MessageWrapper> messages = chat.getMessages();
        for(int i = 0; i<100; i++){
            if(i == messages.size()){
                break;
            }
            Optional<String> returnedWord = findWordInnerText(messages.get(i).getContent(), text);
            if(returnedWord.isPresent()){
                ChatDTO chatDTO = convertor.convertToChatDTO(chat, usernameOfUser);
                if(chatDTO == null){
                    return Optional.empty();
                }
                chatDTO.setLastMessageText(returnedWord.get());
                return Optional.of(chatDTO);
            }
        }
        return Optional.empty();
    }

    private int isPrivateChat(User member1, User member2){
        List<Chat> chatsOfUser = ChatMember.getChatsOfUser(member1);
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

    public List<ChatDTO> getForwardChats(String username) {
        return convertor.convertToChatDTO(userService.findChatsByUsername(username), username);
    }
}
