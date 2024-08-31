package com.example.Messenger.util;

import com.example.Messenger.DAO.chat.ChatDAO;
import com.example.Messenger.DAO.message.ContainerOfMessagesDAO;
import com.example.Messenger.DAO.message.MessageWrapperDAO;
import com.example.Messenger.DAO.user.MessengerUserDAO;
import com.example.Messenger.DAO.user.UserDAO;
import com.example.Messenger.balancers.BalancerOfFoundChats;
import com.example.Messenger.dto.chat.ChatDTO;
import com.example.Messenger.dto.MainWindowInfoDTO;
import com.example.Messenger.dto.chat.InfoOfChatDTO;
import com.example.Messenger.dto.chat.SearchedChatsAndUsersDTO;
import com.example.Messenger.dto.message.ContainerOfMessagesDTO;
import com.example.Messenger.dto.rest.bot.response.message.InfoByImageMessageDTO;
import com.example.Messenger.dto.rest.bot.response.message.InfoByTextMessageDTO;
import com.example.Messenger.dto.message.BlockMessageDTO;
import com.example.Messenger.dto.message.MessageWrapperDTO;
import com.example.Messenger.dto.message.rest.ForwardMessageResponseDTO;
import com.example.Messenger.dto.user.UserDTO;
import com.example.Messenger.dto.util.MessagesByDateDTO;
import com.example.Messenger.models.chat.*;
import com.example.Messenger.models.message.*;
import com.example.Messenger.models.user.ChatMember;
import com.example.Messenger.models.user.User;
import com.example.Messenger.repositories.database.chat.ChatRepository;
import com.example.Messenger.repositories.database.user.UserRepository;
import com.example.Messenger.services.database.message.PhotoMessageService;
import com.example.Messenger.services.database.user.UserService;
import com.example.Messenger.services.email.redis.languageOfApp.LanguageOfAppService;
import com.example.Messenger.services.redis.message.ContainerOfMessagesCacheManager;
import com.example.Messenger.util.abstractClasses.InfoOfMessage;
import com.example.Messenger.util.abstractClasses.MessageSpecification;
import com.example.Messenger.util.enums.ChatMemberType;
import com.example.Messenger.exceptions.chat.ChatContainersOfMessagesIsEmpty;
import com.example.Messenger.exceptions.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class Convertor {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final PhotoMessageService photoMessageService;
    private final MessageWrapperDAO messageWrapperDAO;
    private final MessengerUserDAO messengerUserDAO;
    private final ChatDAO chatDAO;
    private final LanguageOfAppService languageOfAppService;
    private final BalancerOfFoundChats balancerOfFoundChats;
    private final ContainerOfMessagesDAO containerOfMessagesDAO;
    private final ContainerOfMessagesCacheManager containerOfMessagesCacheManager;
    private final Sorter sorter;
    private final ModelMapper modelMapper;
    private final UserDAO userDAO;

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


    public List<MessageWrapperDTO> convertToMessageDTO(List<MessageWrapper> messages){
        List<MessageWrapperDTO> messageWrapperDTOS = new LinkedList<>();

        for(MessageWrapper message: messages){
            String sendingTime = convertTimeToString(message.getSendingTime());
            MessageSpecification specification = messageWrapperDAO.getSpecificationOfMessage(message);
            UserDTO owner = convertToUserDTO((User) message.getOwner());

            MessageWrapperDTO messageDTO  = MessageWrapperDTO.builder()
                    .id(message.getId())
                    .content(message.getContent())
                    .type(message.getType().name().toLowerCase())
                    .sendingTime(sendingTime)
                    .specification(specification)
                    .owner(owner)
                    .build();
        }

        return messageWrapperDTOS;
    }

    private String convertTimeToString(Date sendingTime) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(sendingTime.getHours()).append(":").append(sendingTime.getMinutes());
        return stringBuilder.toString();
    }

    public List<InfoOfMessage> convertToInfoOfMessage(List<MessageWrapper> messages){
        List<InfoOfMessage> willReturnInfoOfMessages = new LinkedList<>();

        messages.forEach(message -> willReturnInfoOfMessages.add(convertToInfoOfMessageByBotDTO(message)));
        return willReturnInfoOfMessages;
    }

    public List<User> convertToUserByUsername(List<String> usernames){
        List<User> users = new ArrayList<>();
        usernames.forEach(username -> users.add(userDAO.findByUsername(username)));
        return users;
    }


    public List<MessagesByDateDTO> convertToMessagesByDateDTO(List<MessageWrapper> allMessagesInChat, String username){
        if(allMessagesInChat.isEmpty()){
            return null;
        }
        List<MessagesByDateDTO> returnMessageList = new LinkedList<>();
        Date beginDate = allMessagesInChat.getLast().getSendingTime();
        Date finishDate = allMessagesInChat.getFirst().getSendingTime();

        while(!equalsTwoDate(beginDate, finishDate)){
            List<MessageWrapper> messagesByDate = getMessagesOfDate(allMessagesInChat, beginDate);

            if(messagesByDate.isEmpty()){
                beginDate = nextDay(beginDate);
                continue;
            }

            MessagesByDateDTO dayOfMessages = new MessagesByDateDTO(beginDate, convertToMessageDTO(messagesByDate));
            returnMessageList.add(dayOfMessages);

            allMessagesInChat = deleteMessageByDateFromAllMessages(allMessagesInChat, messagesByDate);

            beginDate = nextDay(beginDate);
        }

        List<MessageWrapper> messagesOfDate = getMessagesOfDate(allMessagesInChat, beginDate);

        MessagesByDateDTO dayOfMessages = new MessagesByDateDTO(beginDate, convertToMessageDTO(messagesOfDate));
        returnMessageList.add(dayOfMessages);

        return returnMessageList;
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
        Chat chatOfBlockMessage = blockMessage.getChat();
        String chatTitle;
        if(chatOfBlockMessage.getClass() == PrivateChat.class){
            chatTitle = messengerUserDAO.getInterlocutorFromChat(chatOfBlockMessage, username).getUsername();
        }else{
            chatTitle = chatOfBlockMessage.getChatTitleName();
        }
        return BlockMessageDTO.builder()
                .id(blockMessage.getId())
                .content(blockMessage.getContent())
                .chat(blockMessage.getChat())
                .chatTitle(chatTitle)
                .build();
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

        System.out.println(simpleDateFormat.format(date1));
        System.out.println(simpleDateFormat.format(date2));

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
        if(allMessages.isEmpty()){
            return Collections.emptyList();
        }

        for(MessageWrapper message: addedMessages){
            System.out.println(message.getContent());
            allMessages.remove(message);
        }

        return allMessages;
    }

    public ForwardMessageResponseDTO convertToForwardMessageResponseDTO(ForwardMessage forwardMessage, String username) {
        ForwardMessageResponseDTO response = new ForwardMessageResponseDTO();
        response.setForwardedChatName(chatDAO.getChatTitle(forwardMessage.getFromChat(),username));
        return response;
    }

    public InfoOfChatDTO convertToInfoOfChatDTO(int chatId, String username, Long containerIdInChat){
        User user = userDAO.findByUsername(username);
        Chat chat = chatDAO.findById(chatId);

        containerIdInChat = checkContainerId(chat, containerIdInChat);

        ContainerOfMessages container = containerOfMessagesDAO.getIdByIdInChat(chat, containerIdInChat);
        ContainerOfMessagesDTO containerDTO = convertToContainerOfMessagesDTO(container);


        return InfoOfChatDTO.builder()
                .chatId(chat.getId())
                .user(user)
                .interlocutorOrGroupOrChannelName(chatDAO.getChatTitle(chat, username))
                .lastOnlineTimeOrMembersCount(getInfoOfLastOnlineTimeOrMembersCount(chat, user))
                .willForwardChats(convertToChatDTO(UserService.FIND_CHATS_BY_USERNAME(user), username))
                .containerOfMessagesDTO(containerDTO)
                .chatType(chat.getClass().getSimpleName())
                .userIsOwner(chatDAO.userIsOwner(chat, username))
                .build();
    }

    private String getInfoOfLastOnlineTimeOrMembersCount(Chat chat, User user){
        if(chat.getClass() == PrivateChat.class) {
            return messengerUserDAO.getLastOnlineTimeAsString( (User) messengerUserDAO.getInterlocutorFromChat(chat, user.getUsername()));
        }else{
            return chat.getChatHeader();
        }
    }

    public MainWindowInfoDTO convertToMainInfoDTO(String username){
        List<Chat> sortedChats = chatDAO.sortChatsByLastMessage(chatDAO.getChatsByUser(userDAO.findByUsername(username)));
        List<ChatDTO> chatDTOList = convertToChatDTO(sortedChats, username);
        Map<String, List<ChatDTO>> mapOfFoundChatBySearchText = balancerOfFoundChats.userFoundedChats(username);

        return MainWindowInfoDTO.builder()
                .language(languageOfAppService.getLanguage(userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("user with username "+username+" not found")).getSettingsOfUser().getLang()))
                .chats(chatDTOList)
                .foundedChatsOfChatName(mapOfFoundChatBySearchText.get("ChatName"))
                .foundedChatsOfMessage(mapOfFoundChatBySearchText.get("MessageText"))
                .foundUsers(balancerOfFoundChats.foundUsers(username))
                .build();
    }

    public ContainerOfMessagesDTO convertToContainerOfMessagesDTO(ContainerOfMessages container){
        List<MessageWrapperDTO> messagesDTO = convertToMessageDTO(container.getMessages());
        ContainerOfMessagesDTO containerDTO = containerOfMessagesCacheManager.getContainer(container, messagesDTO);
        containerDTO.setMessages(sorter.sortMessageWrapperDTO(containerDTO.getMessages()));
        return containerDTO;
    }

    public ContainerOfMessagesDTO convertToContainerOfMessagesDTOWithoutCaching(ContainerOfMessages container, String username){
        return new ContainerOfMessagesDTO(container.getId(), container.getIdInChat(), convertToMessageDTO(container.getMessages()));
    }

    private long checkContainerId(Chat chat, Long containerId) {
        if(containerId == null || containerId == 0){
            if(chat.getContainerOfMessages().isEmpty()){
                throw new ChatContainersOfMessagesIsEmpty();
            }
            return chat.getContainerOfMessages().getLast().getIdInChat();
        }
        return containerId;
    }

    public UserDTO convertToUserDTO(User user){
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        userDTO.setLastTime(convertTimeToString(user.getLastOnlineTime()));
        userDTO.setIconUrl(user.getLinkOfIcon());
        return userDTO;
    }

    public List<UserDTO> convertToUserDTO(List<User> users){
        List<UserDTO> userDTOList = new ArrayList<>();
        users.forEach(user -> userDTOList.add(convertToUserDTO(user)));
        return userDTOList;
    }

    public SearchedChatsAndUsersDTO convertToSearchedChatsAndUsersDTO(List<Chat> foundChatsByTitle, List<Chat> foundChatsByMessages,
                                                                      List<User> foundUsers, String username) {
        List<ChatDTO> chatDTOOfFoundChatsByTitle = convertToChatDTO(foundChatsByTitle, username);
        List<ChatDTO> chatDTOOfFoundChatsByMessages = convertToChatDTO(foundChatsByMessages, username);
        List<UserDTO> userDTOOfFoundUsers = convertToUserDTO(foundUsers);
        return SearchedChatsAndUsersDTO.builder()
                .foundChatsByTitle(chatDTOOfFoundChatsByTitle)
                .foundChatsByMessages(chatDTOOfFoundChatsByMessages)
                .foundUsers(userDTOOfFoundUsers)
                .build();
    }
}
