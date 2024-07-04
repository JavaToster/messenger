package com.example.Messenger.util;

import com.example.Messenger.DAO.chat.ChatDAO;
import com.example.Messenger.DAO.message.MessageWrapperDAO;
import com.example.Messenger.DAO.user.MessengerUserDAO;
import com.example.Messenger.dto.ChatDTO;
import com.example.Messenger.dto.chat.InfoOfChatDTO;
import com.example.Messenger.dto.chat.chatHead.privateChat.PrivateChatDTO;
import com.example.Messenger.dto.rest.bot.response.message.InfoByImageMessageDTO;
import com.example.Messenger.dto.rest.bot.response.message.InfoByTextMessageDTO;
import com.example.Messenger.dto.message.BlockMessageDTO;
import com.example.Messenger.dto.message.MessageResponseDTO;
import com.example.Messenger.dto.message.rest.ForwardMessageResponseDTO;
import com.example.Messenger.dto.user.InfoOfUserDTO;
import com.example.Messenger.dto.util.MessagesByDateDTO;
import com.example.Messenger.models.chat.*;
import com.example.Messenger.models.message.*;
import com.example.Messenger.models.user.ChatMember;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.models.user.User;
import com.example.Messenger.repositories.database.chat.BotChatRepository;
import com.example.Messenger.repositories.database.chat.ChatRepository;
import com.example.Messenger.repositories.database.user.UserRepository;
import com.example.Messenger.services.database.message.PhotoMessageService;
import com.example.Messenger.services.database.user.UserService;
import com.example.Messenger.util.abstractClasses.InfoOfMessage;
import com.example.Messenger.util.enums.ChatMemberType;
import com.example.Messenger.util.exceptions.UserNotMemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class Convertor {

    private final BotChatRepository botChatRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final PhotoMessageService photoMessageService;
    private final MessageWrapperDAO messageWrapperDAO;
    private final MessengerUserDAO messengerUserDAO;
    private final ChatDAO chatDAO;

    public static InfoOfUserDTO convertToInfoOfUser(User user) {
        return new InfoOfUserDTO(user.getId(), user.getUsername(), user.getName(), user.getLastname(), user.getEmail(), user.getLinkOfIcon());
    }

    public List<ChatDTO> convertToChatDTO(List<Chat> chats, String username){
        List<ChatDTO> chatsDTO = new LinkedList<>();
        for(Chat chat: chats){
            ChatDTO chatDTO = new ChatDTO(chat);
            chatDTO.setBannedChat(isBan(username, chat));
            chatDTO.setChatTitle(chatDAO.getChatTitle(chat, username));
            chatsDTO.add(chatDTO);
        }

        return chatsDTO;
    }


    public ChatDTO convertToChatDTO(Chat chat, String username){
        ChatDTO chatDTO = new ChatDTO(chat);
        chatDTO.setBannedChat(isBan(username, chat));
        chatDTO.setChatTitle(chatDAO.getChatTitle(chat, username));
        return chatDTO;
    }

    public List<BlockMessageDTO> convertToChatDTOOfBlockMessage(List<BlockMessage> blockMessages, String username){
        List<BlockMessageDTO> blockMessageDTOList = new LinkedList<>();
        for(BlockMessage blockMessage: blockMessages){
            blockMessageDTOList.add(convertToBlockMessageDTO(blockMessage, username));
        }
        return blockMessageDTOList;
    }


    public List<MessageResponseDTO> convertToMessageDTO(List<MessageWrapper> messages, String username){
        List<MessageResponseDTO> messageResponseDTOS = new LinkedList<>();

        for(MessageWrapper message: messages){
            MessageResponseDTO messageResponseDTO = new MessageResponseDTO(message);
            messageResponseDTO.setSpecification(messageWrapperDAO.getSpecificationOfMessage(message));
            messageResponseDTO.setUserIsOwner(messageWrapperDAO.userIsMessageOwner(message, username));
            messageResponseDTOS.add(messageResponseDTO);
        }

        return messageResponseDTOS;
    }

    public List<InfoOfMessage> convertToInfoOfMessage(List<MessageWrapper> messages){
        List<InfoOfMessage> willReturnInfoOfMessages = new LinkedList<>();

        messages.forEach(message -> willReturnInfoOfMessages.add(convertToInfoOfMessageByBotDTO(message)));
        return willReturnInfoOfMessages;
    }

    public List<User> convertToUserByUsername(List<String> usernames){
        List<User> users = new ArrayList<>();
        usernames.forEach(username -> users.add(getUser(username)));
        return users;
    }


    public List<MessagesByDateDTO> convertToMessagesByDateDTO(List<MessageWrapper> allMessagesInChat, String username){
        List<MessagesByDateDTO> returnMessageList = new ArrayList<>();
        Date beginDate = allMessagesInChat.getLast().getSendingTime();
        Date finishDate = allMessagesInChat.getFirst().getSendingTime();

        while(!equalsTwoDate(beginDate, finishDate)){
            List<MessageWrapper> messagesByDate = getMessagesOfDate(allMessagesInChat, beginDate);

            if(messagesByDate.isEmpty()){
                beginDate = nextDay(beginDate);
                continue;
            }

            MessagesByDateDTO dayOfMessages = new MessagesByDateDTO(beginDate, convertToMessageDTO(messagesByDate, username));
            returnMessageList.add(dayOfMessages);

            allMessagesInChat = deleteMessageByDateFromAllMessages(allMessagesInChat, messagesByDate);

            beginDate = nextDay(beginDate);
        }

        List<MessageWrapper> messagesOfDate = getMessagesOfDate(allMessagesInChat, beginDate);

        MessagesByDateDTO dayOfMessages = new MessagesByDateDTO(beginDate, convertToMessageDTO(messagesOfDate, username));
        returnMessageList.add(dayOfMessages);

        return returnMessageList.reversed();
    }

    /** приватные методы для public методов */

    private boolean isBan(String username, Chat chat){
        List<ChatMember> banMembers = chatRepository.findById(chat.getId()).orElse(null).getMembers();
        for(ChatMember chatMember: banMembers){
            if(chatMember.getUser().equals(username) && chatMember.getMemberType() == ChatMemberType.BLOCK){
                return true;
            }
        }
        return false;
    }

    private BlockMessageDTO convertToBlockMessageDTO(BlockMessage blockMessage, String username){
        BlockMessageDTO blockMessageDTO = new BlockMessageDTO();
        blockMessageDTO.setId(blockMessageDTO.getId());
        blockMessageDTO.setText(blockMessageDTO.getText());
        blockMessageDTO.setChatTitle(chatDAO.getChatTitle(blockMessage.getChat(), username));
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

        return simpleDateFormat.format(date1).equals(simpleDateFormat.format(date2));
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

    private List<MessageWrapper> deleteMessageByDateFromAllMessages(List<MessageWrapper> allMessages, List<MessageWrapper> addedMessages) {
        addedMessages.forEach(allMessages::remove);

        return allMessages;
    }

    public ForwardMessageResponseDTO convertToForwardMessageResponseDTO(ForwardMessage forwardMessage, String username) {
        ForwardMessageResponseDTO response = new ForwardMessageResponseDTO();
        response.setForwardedChatName(chatDAO.getChatTitle(forwardMessage.getFromChat(),username));
        return response;
    }

    public InfoOfChatDTO convertToInfoOfChatDTO(Chat chat, String username){
        User user = getUser(username);
        List<MessagesByDateDTO> messagesByDateDTOS = convertToMessagesByDateDTO(messageWrapperDAO.sortMessagesById(chat.getMessages()), username);
        String interlocutorOfGroupOrChannelName = chatDAO.getChatTitle(chat, username);
        String lastTimeOnlineOrMembersCount = getInfoOfLastOnlineTimeOrMembersCount(chat, user);
        List<ChatDTO> willForwardChats = convertToChatDTO(UserService.FIND_CHATS_BY_USERNAME(user), username);
        return new InfoOfChatDTO(chat, user, interlocutorOfGroupOrChannelName, lastTimeOnlineOrMembersCount, willForwardChats, messagesByDateDTOS);
    }

    private String getInfoOfLastOnlineTimeOrMembersCount(Chat chat, User user){
        if(chat.getClass() == PrivateChat.class) {
            return messengerUserDAO.getLastOnlineTimeAsString( (User) messengerUserDAO.getInterlocutorFromChat(chat, user.getUsername()));
        }else{
            return chat.getChatHeader();
        }
    }

    private User getUser(String username){
        return userRepository.findByUsername(username).orElse(null);
    }
}
