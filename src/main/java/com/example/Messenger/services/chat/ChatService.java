package com.example.Messenger.services.chat;

import com.example.Messenger.dto.ChatDTO;
import com.example.Messenger.dto.user.FoundUserOfUsername;
import com.example.Messenger.models.cache.LanguageOfApp;
import com.example.Messenger.models.chat.*;
import com.example.Messenger.models.message.Message;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.models.user.ChatMember;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.models.user.User;
import com.example.Messenger.repositories.chat.ChannelRepository;
import com.example.Messenger.repositories.chat.ChatRepository;
import com.example.Messenger.repositories.chat.GroupChatRepository;
import com.example.Messenger.repositories.chat.PrivateChatRepository;
import com.example.Messenger.repositories.user.UserRepository;
import com.example.Messenger.services.user.MessengerUserService;
import com.example.Messenger.services.user.UserService;
import com.example.Messenger.util.Convertor;
import com.example.Messenger.util.exceptions.ChatNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
            if(textInnerOtherText(chat.getChatTitle(), findText).isPresent()){
                willReturnChats.add(chat);
            }
        }

        return willReturnChats;
    }

    private Optional<String> textInnerOtherText(String beforeText, String word){
        String text = beforeText.toLowerCase();

        System.out.println(text);
        System.out.println(word);

        int textLength = text.length();
        char firstCharacterOfText = word.toLowerCase().charAt(0);
        int willEqualedTextLength = word.length();

        if(willEqualedTextLength>textLength){
            return Optional.empty();
        }

        while(textLength>=word.length()){
            int indexWillFindCharacterInText = text.indexOf(firstCharacterOfText);
            if(indexWillFindCharacterInText == -1){
                return Optional.empty();
            }

            text = text.substring(indexWillFindCharacterInText);
            String substringText;

            indexWillFindCharacterInText = text.indexOf(firstCharacterOfText);
            if(text.length() < willEqualedTextLength){
                return Optional.empty();
            }
            try {
                substringText = text.substring(indexWillFindCharacterInText - 1, indexWillFindCharacterInText + willEqualedTextLength - 1);
            }catch (StringIndexOutOfBoundsException e){
                substringText = text.substring(indexWillFindCharacterInText, indexWillFindCharacterInText+willEqualedTextLength);
            }

            if(substringText.equals(word.toLowerCase())){
                return Optional.of(beforeText);
            }

            text = text.substring(indexWillFindCharacterInText+willEqualedTextLength);
        }
        if(beforeText.toLowerCase().equals(word.toLowerCase()))
            return Optional.of(beforeText);
        return Optional.empty();
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
            if(textInnerOtherText(user.getUsername(), text).isPresent()){
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
            Optional<String> returnedWord = textInnerOtherText(messages.get(i).getContent(), text);
            if(returnedWord.isPresent()){
                ChatDTO chatDTO = convertor.convertToChatDTO(chat, usernameOfUser);
                if(chatDTO == null){
                    return Optional.empty();
                }
                chatDTO.getLastMessage().setContent(returnedWord.get());
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
