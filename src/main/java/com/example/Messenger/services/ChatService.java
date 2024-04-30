package com.example.Messenger.services;

import com.example.Messenger.dto.ChatDTO;
import com.example.Messenger.dto.chatHead.ChatHeadDTO;
import com.example.Messenger.dto.message.FoundMessageDTO;
import com.example.Messenger.dto.user.FoundUserOfUsername;
import com.example.Messenger.models.*;
import com.example.Messenger.models.cache.LanguageOfApp;
import com.example.Messenger.repositories.*;
import com.example.Messenger.util.MessengerMapper;
import com.example.Messenger.util.enums.LanguageType;
import com.example.Messenger.util.exceptions.ChatNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final GroupChatRepository groupChatRepository;
    private final ChannelRepository channelRepository;
    private final UserService userService;
    private final BotChatService botChatService;
    private final MessengerUserService messengerUserService;
    private final PrivateChatService privateChatService;
    private final GroupChatService groupChatService;
    private final ChannelService channelService;
    private final PrivateChatRepository privateChatRepository;
    @Autowired
    public ChatService(ChatRepository chatRepository, UserRepository userRepository, GroupChatRepository groupChatRepository, ChannelRepository channelRepository, UserService userService, BotChatService botChatService, MessengerUserService messengerUserService, PrivateChatService privateChatService, GroupChatService groupChatService, ChannelService channelService, PrivateChatRepository privateChatRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.groupChatRepository = groupChatRepository;
        this.channelRepository = channelRepository;
        this.userService = userService;
        this.botChatService = botChatService;
        this.messengerUserService = messengerUserService;
        this.privateChatService = privateChatService;
        this.groupChatService = groupChatService;
        this.channelService = channelService;
        this.privateChatRepository = privateChatRepository;
    }

    public Chat findById(int id) {
        return chatRepository.findById(id). orElseThrow(ChatNotFoundException::new);
    }

    public List<Chat> findByMemberUsername(String username) {
        List<Chat> chats = new ArrayList<>();
        userRepository.findByUsername(username).orElse(null).getMembers().forEach(chatMember -> chats.add(chatMember.getChat()));
        return chats;
    }

    public List<ChatDTO> convertToChatDTO(List<Chat> chats, String username){
        List<ChatDTO> chatsDTO = new ArrayList<>();
        for(Chat chat: chats){
            if(!chat.getClass().equals(Channel.class)) {
                if (chat.getMessages() == null | chat.getMessages().size() == 0) {
                    continue;
                }
            }

            ChatDTO chatDTO = new ChatDTO(chat);
            if(chat.getClass().equals(PrivateChat.class)){
                chatDTO.setChatTitle(getInterlocutor(username, chat).getUsername());
            } else if (chat.getClass().equals(GroupChat.class)) {
                chatDTO.setChatTitle(groupChatRepository.findById(chat.getId()).orElse(null).getGroupName());
            } else if(chat.getClass().equals(Channel.class)){
                chatDTO.setChatTitle(channelRepository.findById(chat.getId()).orElse(null).getName());
            }else if(chat.getClass().equals(BotChat.class)){
                chatDTO.setChatTitle(botChatService.getBotName(chat.getId()));
            }


            if(userService.isBan(username, chat)){
                chatDTO.setBannedChat(true);
            }

            try {
                chatDTO.setLastMessageSendTime(addZero(chat.getMessages().getLast().getSendingTime().getHours()) + ":" + addZero(chat.getMessages().getLast().getSendingTime().getMinutes()));
            }catch (NoSuchElementException ignored){}

            chatsDTO.add(chatDTO);
        }

        return chatsDTO;
    }

    public ChatDTO convertToChatDTO(Chat chat, String username){
        if(!chat.getClass().equals(Channel.class)) {
            if (chat.getMessages() == null | chat.getMessages().size() == 0) {
                return null;
            }
        }

        ChatDTO chatDTO = new ChatDTO(chat);
        if(chat.getClass().equals(PrivateChat.class)){
            chatDTO.setChatTitle(getInterlocutor(username, chat).getUsername());
        }else if (chat.getClass().equals(GroupChat.class)) {
            chatDTO.setChatTitle(groupChatRepository.findById(chat.getId()).orElse(null).getGroupName());
        }else if(chat.getClass().equals(Channel.class)){
            chatDTO.setChatTitle(channelRepository.findById(chat.getId()).orElse(null).getName());
        }else if(chat.getClass().equals(BotChat.class)){
            chatDTO.setChatTitle(botChatService.getBotName(chat.getId()));
        }

        if(userService.isBan(username, chat)){
            chatDTO.setBannedChat(true);
        }

        try {
            chatDTO.setLastMessageSendTime(addZero(chat.getMessages().getLast().getSendingTime().getHours()) + ":" + addZero(chat.getMessages().getLast().getSendingTime().getMinutes()));
        }catch (NoSuchElementException ignored){}

        return chatDTO;
    }

    @Deprecated
    public List<ChatDTO> convertToChatDTO(String username) {
        List<ChatDTO> chatsDTO = new ArrayList<>();
        List<Chat> chats = new ArrayList<>();

        for(ChatMember chatMember: userRepository.findByUsername(username).orElse(null).getMembers()){
            chats.add(chatMember.getChat());
        }

        for(Chat chat: chats){
            if(!chat.getClass().equals(Channel.class)) {
                if (chat.getMessages() == null | chat.getMessages().size() == 0) {
                    continue;
                }
            }

            ChatDTO chatDTO = new ChatDTO(chat);
            if(chat.getClass().equals(PrivateChat.class)){
                chatDTO.setChatTitle(getInterlocutor(username, chat).getUsername());
            } else if (chat.getClass().equals(GroupChat.class)) {
                chatDTO.setChatTitle(groupChatRepository.findById(chat.getId()).orElse(null).getGroupName());
            } else if(chat.getClass().equals(Channel.class)){
                chatDTO.setChatTitle(channelRepository.findById(chat.getId()).orElse(null).getName());
            }

            chatDTO.setLastMessageSendTime(addZero(chat.getMessages().getLast().getSendingTime().getHours())+":"+addZero(chat.getMessages().getLast().getSendingTime().getMinutes()));

            chatsDTO.add(chatDTO);
        }
        return chatsDTO;
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

    @Transactional(readOnly = true)
    public List<Chat> findAll() {
        return chatRepository.findAll();
    }

    @Transactional
    public synchronized void deleteEmptyChats(){
        List<Chat> emptyChats = chatRepository.findAll();
        try{
            emptyChats.removeIf(chat -> !chat.getMessages().isEmpty());
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
            int id = isPrivateChat(userService.findById(user.getId()), userService.findByUsername(username));
            if(id == -1)
                id = privateChatService.createNewChat((User)user, userService.findByUsername(username));
            return id;
        }else{
            return botChatService.createNewChat(userService.findByUsername(username), user);
        }
    }

    public List<ChatDTO> findChatsBySearchTextByChatName(String findText, String usernameOfUser) {
        return sortedChatsBySearchedText(convertToChatDTO(sortChatsByLastMessage(usernameOfUser), usernameOfUser), findText);
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
        List<Message> messages = chat.getMessages();
        for(int i = 0; i<100; i++){
            if(i == messages.size()){
                break;
            }
            Optional<String> returnedWord = textInnerOtherText(messages.get(i).getMessageText(), text);
            if(returnedWord.isPresent()){
                ChatDTO chatDTO = convertToChatDTO(chat, usernameOfUser);
                if(chatDTO == null){
                    return Optional.empty();
                }
                chatDTO.getLastMessage().setMessageText(returnedWord.get());
                return Optional.of(chatDTO);
            }
        }
        return Optional.empty();
    }

    private int isPrivateChat(User member1, User member2){
        List<PrivateChat> chats = privateChatRepository.findAll();
        for(PrivateChat chat: chats){
            List<ChatMember> members = chat.getMembers();
            if(members.get(0).getUser().getId() == member1.getId() && members.get(1).getUser().getId() == member2.getId()){
                return chat.getId();
            }
        }

        return -1;
    }
}
