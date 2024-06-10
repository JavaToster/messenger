package com.example.Messenger.util;

import com.example.Messenger.dto.ChatDTO;
import com.example.Messenger.dto.bot.response.message.InfoByImageMessageDTO;
import com.example.Messenger.dto.bot.response.message.InfoByTextMessageDTO;
import com.example.Messenger.dto.message.BlockMessageDTO;
import com.example.Messenger.dto.message.MessageResponseDTO;
import com.example.Messenger.dto.message.rest.ForwardMessageResponseDTO;
import com.example.Messenger.dto.message.util.ForwardMessageSpecification;
import com.example.Messenger.dto.message.util.ImageMessageSpecification;
import com.example.Messenger.dto.message.util.LinkMessageSpecification;
import com.example.Messenger.dto.user.InfoOfUserDTO;
import com.example.Messenger.dto.util.DateDayOfMessagesDTO;
import com.example.Messenger.models.chat.*;
import com.example.Messenger.models.message.*;
import com.example.Messenger.models.user.Bot;
import com.example.Messenger.models.user.ChatMember;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.models.user.User;
import com.example.Messenger.repositories.chat.BotChatRepository;
import com.example.Messenger.repositories.chat.ChannelRepository;
import com.example.Messenger.repositories.chat.ChatRepository;
import com.example.Messenger.repositories.chat.GroupChatRepository;
import com.example.Messenger.repositories.message.PhotoMessageRepository;
import com.example.Messenger.repositories.user.UserRepository;
import com.example.Messenger.services.message.MessageWrapperService;
import com.example.Messenger.services.message.PhotoMessageService;
import com.example.Messenger.util.abstractClasses.InfoOfMessage;
import com.example.Messenger.util.enums.ChatMemberType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class Convertor {

    private final GroupChatRepository groupChatRepository;
    private final ChannelRepository channelRepository;
    private final BotChatRepository botChatRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final PhotoMessageRepository photoMessageRepository;
    private final PhotoMessageService photoMessageService;
    @Autowired
    public Convertor(GroupChatRepository groupChatRepository, ChannelRepository channelRepository, BotChatRepository botChatRepository, ChatRepository chatRepository, UserRepository userRepository, PhotoMessageRepository photoMessageRepository, PhotoMessageService photoMessageService){
        this.groupChatRepository = groupChatRepository;
        this.channelRepository = channelRepository;
        this.botChatRepository = botChatRepository;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.photoMessageRepository = photoMessageRepository;
        this.photoMessageService = photoMessageService;
    }


    public List<ChatDTO> convertToChatDTO(List<Chat> chats, String username){
        List<ChatDTO> chatsDTO = new ArrayList<>();
        for(Chat chat: chats){
            ChatDTO chatDTO = new ChatDTO(chat);
            if(!chat.getClass().equals(Channel.class)) {
                if (chat.getMessages() == null || chat.getMessages().isEmpty()) {
                    continue;
                }
            }else{
                if(chat.getMessages() == null || chat.getMessages().isEmpty()){
                    chatDTO.setLastMessageText("nothing here");
                    chatDTO.setChatTitle(((Channel) chat).getName());
                    chatsDTO.add(chatDTO);
                    continue;
                }
            }

            MessageWrapper lastMessage;
            try {
                lastMessage = MessageWrapperService.sortMessagesById(chat.getMessages()).getLast();
            }catch (NoSuchElementException e){
                continue;
            }
            if(lastMessage.getClass() == Message.class || lastMessage.getClass() == LinkMessage.class){
                chatDTO.setLastMessageText(lastMessage.getContent());
            }else if(lastMessage.getClass() == ImageMessage.class){
                chatDTO.setLastMessageText("image");
            }else if(lastMessage.getClass() == ForwardMessage.class){
                if(((ForwardMessage) lastMessage).getForwardMessageType().equals("text")){
                    chatDTO.setLastMessageText(lastMessage.getContent());
                }else{
                    chatDTO.setLastMessageText("image");
                }
            }else{
                throw new RuntimeException("type of message not support");
            }
            if(chat.getClass().equals(PrivateChat.class)){
                chatDTO.setChatTitle(getInterlocutor(username, chat).getUsername());
            } else if (chat.getClass().equals(GroupChat.class)) {
                chatDTO.setChatTitle(groupChatRepository.findById(chat.getId()).orElse(null).getGroupName());
            } else if(chat.getClass().equals(Channel.class)){
                chatDTO.setChatTitle(channelRepository.findById(chat.getId()).orElse(null).getName());
            }else if(chat.getClass().equals(BotChat.class)){
                chatDTO.setChatTitle(getBotName(chat.getId()));
            }


            if(isBan(username, chat)){
                chatDTO.setBannedChat(true);
            }

            try {
                chatDTO.setLastMessageSendTime(addZero(chat.getMessages().getLast().getSendingTime().getHours()) + ":" + addZero(chat.getMessages().getLast().getSendingTime().getMinutes()));
            }catch (NoSuchElementException ignored){
                System.out.println(3);
            }

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
            chatDTO.setChatTitle(getBotName(chat.getId()));
        }

        if(isBan(username, chat)){
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

    public List<BlockMessageDTO> convertToChatDTOOfBlockMessage(List<BlockMessage> blockMessages, String username){
        List<BlockMessageDTO> blockMessageDTOList = new ArrayList<>();
        for(BlockMessage blockMessage: blockMessages){
            Chat chat = blockMessage.getChat();
            String title;
            if(chat.getClass() == PrivateChat.class){
                title = getInterlocutor(username, chat).getUsername();
            }else if(chat.getClass() == GroupChat.class){
                title = groupChatRepository.findById(chat.getId()).orElse(null).getGroupName();
            }else{
                title = channelRepository.findById(chat.getId()).orElse(null).getName();
            }
            BlockMessageDTO blockMessageDTO = convertToBlockMessageDTO(blockMessage);
            blockMessageDTO.setChatTitle(title);

            blockMessageDTOList.add(blockMessageDTO);
        }
        return blockMessageDTOList;
    }


    public List<MessageResponseDTO> convertToMessageDTO(List<MessageWrapper> messages, String username){
        List<MessageResponseDTO> messageResponseDTOS = new ArrayList<>();

        for(MessageWrapper message: messages){
            MessageResponseDTO messageResponseDTO = new MessageResponseDTO(message, message.getOwner());
            messageResponseDTO.setDate(addZero(message.getSendingTime().getHours())+":"+addZero(message.getSendingTime().getMinutes()));
            if(message.getClass() == ImageMessage.class){
                messageResponseDTO.setSpecification(new ImageMessageSpecification(photoMessageRepository.findById(message.getId()).orElse(null).getTextUnderPhoto()));
            }else if(message.getClass() == ForwardMessage.class){
                ForwardMessage forwardMessage = (ForwardMessage) message;
                if(forwardMessage.getForwardMessageType().equals("image")) {
                    messageResponseDTO.setSpecification(new ForwardMessageSpecification(forwardMessage.getForwardMessageType(), forwardMessage.getFromOwner().getUsername(), forwardMessage.getTextUnderMessage()));
                }else{
                    messageResponseDTO.setSpecification(new ForwardMessageSpecification(forwardMessage.getForwardMessageType(), forwardMessage.getFromOwner().getUsername()));
                }
            }else if(message.getClass() == LinkMessage.class){
                LinkMessage link = (LinkMessage) message;
                messageResponseDTO.setSpecification(new LinkMessageSpecification(link.getLink()));
            }
            if(message.getOwner().equals(username)){
                messageResponseDTO.setUserIsOwner(true);
            }
            messageResponseDTOS.add(messageResponseDTO);
        }


        return messageResponseDTOS;
    }

    public List<DateDayOfMessagesDTO> convertToMessageDayDTO(List<MessageWrapper> messages, String username){
        return convertToMessagesDTO(messages, username);
    }


    public List<InfoOfMessage> convertToInfoOfMessage(List<MessageWrapper> messages){
        List<InfoOfMessage> responseList = new LinkedList<>();

        messages.forEach(messageWrapper -> System.out.println(messageWrapper.getContent()));

        messages.forEach(message -> responseList.add(convertToInfoOfMessageByBotDTO(message)));
        return responseList;
    }

    public List<User> convertToUser(List<String> usernames){
        List<User> users = new ArrayList<>();
        usernames.forEach(username -> users.add(userRepository.findByUsername(username).orElse(null)));
        return users;
    }


    public List<DateDayOfMessagesDTO> convertToMessagesDTO(List<MessageWrapper> messages, String username){
        List<DateDayOfMessagesDTO> returnMessageList = new ArrayList<>();
        Date beginDate = messages.getLast().getSendingTime();
        Date finishDate = messages.getFirst().getSendingTime();

        while(!equalsTwoDate(beginDate, finishDate)){
            List<MessageWrapper> messagesOfDate = getMessagesOfDate(messages, beginDate);

            if(messagesOfDate.isEmpty()){
                beginDate = nextDay(beginDate);
                continue;
            }

            DateDayOfMessagesDTO dayOfMessages = new DateDayOfMessagesDTO(beginDate, convertToMessageDTO(messagesOfDate, username));
            returnMessageList.add(dayOfMessages);

            messages = deleteAddedMessages(messages, messagesOfDate);

            beginDate = nextDay(beginDate);
        }

        List<MessageWrapper> messagesOfDate = getMessagesOfDate(messages, beginDate);

        DateDayOfMessagesDTO dayOfMessages = new DateDayOfMessagesDTO(beginDate, convertToMessageDTO(messagesOfDate, username));
        returnMessageList.add(dayOfMessages);

        return returnMessageList.reversed();
    }


    public InfoOfUserDTO convertToInfoOfUserDTO(User byUsername) {
        return new InfoOfUserDTO(byUsername.getId(), byUsername.getUsername(), byUsername.getName(), byUsername.getLastname(), byUsername.getEmail(), byUsername.getIcon()==null?"":byUsername.getIcon().getLink());
    }

    /** приватные методы для public методов */


    private MessengerUser getInterlocutor(String username, Chat chat){
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

    private String getBotName(int chatId) {
        List<ChatMember> members = botChatRepository.findById(chatId).orElse(null).getMembers();
        for(ChatMember member: members){
            if(member.getUser().getClass() == Bot.class){
                return member.getUser().getUsername();
            }
        }
        return "";
    }

    private boolean isBan(String username, Chat chat){
        List<ChatMember> banMembers = chatRepository.findById(chat.getId()).orElse(null).getMembers();
        for(ChatMember chatMember: banMembers){
            if(chatMember.getUser().equals(username) && chatMember.getMemberType() == ChatMemberType.BLOCK){
                return true;
            }
        }
        return false;
    }

    private String addZero(int time){
        return time < 10 ? "0"+time : ""+time;
    }

    private BlockMessageDTO convertToBlockMessageDTO(BlockMessage blockMessage){
        BlockMessageDTO blockMessageDTO = new BlockMessageDTO();
        blockMessageDTO.setId(blockMessageDTO.getId());
        blockMessageDTO.setText(blockMessageDTO.getText());
        return blockMessageDTO;
    }

    private InfoOfMessage convertToInfoOfMessageByBotDTO(MessageWrapper message){
        if(message.getClass() == Message.class){
            return new InfoByTextMessageDTO(message.getChat().getId(), message.getContent());
        }else{
            try {
                return new InfoByImageMessageDTO(message.getId(), photoMessageService.getBytesOfImage(message));
            }catch (IOException e){
                System.out.println("errrrooorr");
                return null;
            }
        }
    }

    private boolean equalsTwoDate(Date date1, Date date2){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM");
        String date1OfString = simpleDateFormat.format(date1);
        String date2OfString = simpleDateFormat.format(date2);

//        System.out.println(date2OfString);

        return date1OfString.equals(date2OfString);
    }

    private List<MessageWrapper> getMessagesOfDate(List<MessageWrapper> messages, Date date){
        List<MessageWrapper> messagesReturn = new LinkedList<>();
        for(MessageWrapper message: messages){

            if(equalsTwoDate(message.getSendingTime(), date)){
                messagesReturn.add(message);
            }
        }
        return messagesReturn;
    }

    private Date nextDay(Date beginDate) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(beginDate);

        calendar.add(Calendar.DAY_OF_MONTH,1);
        return calendar.getTime();
    }

    private List<MessageWrapper> deleteAddedMessages(List<MessageWrapper> allMessages, List<MessageWrapper> addedMessages){
        try {
            addedMessages.forEach(allMessages::remove);
        }catch (Exception e){

        }
        return allMessages;
    }

    private void initialize(MessageWrapper message){
        Chat chat = message.getChat();
        if(chat.getClass() == Channel.class){
            message.setChat(channelRepository.findById(chat.getId()).orElse(null));
        }
    }

    public ForwardMessageResponseDTO convertToForwardMessageResponseDTO(ForwardMessage forwardMessage, String username) {
        ForwardMessageResponseDTO response = new ForwardMessageResponseDTO();
        Chat chatOfForward = forwardMessage.getChat();
        if(chatOfForward.getClass() == Channel.class){
            response.setForwardedChatName(((Channel) chatOfForward).getName());
        }else if(chatOfForward.getClass() == GroupChat.class){
            response.setForwardedChatName(((GroupChat) chatOfForward).getGroupName());
        }else{
            response.setForwardedChatName(getInterlocutor(username, forwardMessage.getChat()).getUsername());
        }
        return response;
    }
}
