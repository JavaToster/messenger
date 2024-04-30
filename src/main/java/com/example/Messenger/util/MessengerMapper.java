package com.example.Messenger.util;

import com.example.Messenger.dto.bot.response.message.InfoOfMessageByBotDTO;
import com.example.Messenger.dto.chatHead.ChatHeadDTO;
import com.example.Messenger.dto.chatHead.botChat.BotChatHeadDTO;
import com.example.Messenger.dto.chatHead.channel.ChannelHeadDTO;
import com.example.Messenger.dto.chatHead.channel.ChannelMemberDTO;
import com.example.Messenger.dto.chatHead.group.GroupChatDTO;
import com.example.Messenger.dto.chatHead.group.GroupChatMemberDTO;
import com.example.Messenger.dto.chatHead.privateChat.PrivateChatDTO;
import com.example.Messenger.dto.message.MessageResponseDTO;
import com.example.Messenger.dto.util.DateDayOfMessagesDTO;
import com.example.Messenger.models.*;
import com.example.Messenger.services.*;
import com.example.Messenger.util.enums.ChatMemberType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class MessengerMapper {

    private final ChannelService channelService;
    private final GroupChatService groupChatService;
    private final ChatService chatService;
    private final UserService userService;
    private final BotService botService;

    @Autowired
    public MessengerMapper(ChannelService channelService, GroupChatService groupChatService, ChatService chatService, UserService userService, BotService botService) {
        this.channelService = channelService;
        this.groupChatService = groupChatService;
        this.chatService = chatService;
        this.userService = userService;
        this.botService = botService;
    }

    //for channelConvert
    private List<ChannelMemberDTO> convertToChannelMemberDTO(List<ChatMember> members){
        List<ChannelMemberDTO> memberDTOList = new LinkedList<>();
        members.forEach(member -> memberDTOList.add(new ChannelMemberDTO(member.getUser().getId(), member.getUsernameOfUser(), member.getMemberType() == ChatMemberType.BLOCK)));
        return memberDTOList;
    }

    @Deprecated
    public ChatHeadDTO map(Chat chat){
        List<ChatMember> members = chat.getMembers();
        if(chat.getClass() == Channel.class){
            return new ChannelHeadDTO(channelService.getChannelName(chat), channelService.countMembers(chat), "someDescription", convertToChannelMemberDTO(members), List.of());
        }else if(chat.getClass() == BotChat.class){
            return new BotChatHeadDTO(botService.getBotName(chat), "Some description of bot");
        }else{
            return new GroupChatDTO(groupChatService.getGroupName(chat), groupChatService.membersCount(chat), "someDescription", convertToGroupMemberDTO(members));
        }
    }

    @Deprecated
    public ChatHeadDTO map(Chat chat, String username){
        return new PrivateChatDTO(chatService.getInterlocutor(username, chat).getUsername(), userService.findByUsername(username).getLastOnline(), "79393826388");
    }


    private List<GroupChatMemberDTO> convertToGroupMemberDTO(List<ChatMember> members){
        List<GroupChatMemberDTO> memberDTOList = new LinkedList<>();
        members.forEach(member -> memberDTOList.add(new GroupChatMemberDTO(member.getUsernameOfUser(), member.getMemberType())));
        return memberDTOList;
    }


    //for messages convert

    public List<DateDayOfMessagesDTO> convertToMessagesDTO(List<Message> messages, String username){
        List<DateDayOfMessagesDTO> returnMessageList = new ArrayList<>();
        Date beginDate = messages.getLast().getSendingTime();
        Date finishDate = messages.getFirst().getSendingTime();

        while(!equalsTwoDate(beginDate, finishDate)){
            List<Message> messagesOfDate = getMessagesOfDate(messages, beginDate);

            if(messagesOfDate.isEmpty()){
                beginDate = nextDay(beginDate);
                continue;
            }

            DateDayOfMessagesDTO dayOfMessages = new DateDayOfMessagesDTO(beginDate, convertToMessageDTO(messagesOfDate, username));
            returnMessageList.add(dayOfMessages);

            messages = deleteAddedMessages(messages, messagesOfDate);

            beginDate = nextDay(beginDate);
        }

        List<Message> messagesOfDate = getMessagesOfDate(messages, beginDate);

        DateDayOfMessagesDTO dayOfMessages = new DateDayOfMessagesDTO(beginDate, convertToMessageDTO(messagesOfDate, username));
        returnMessageList.add(dayOfMessages);

        System.out.println(returnMessageList.getLast().getMessages().getLast().getMessageText());

        return returnMessageList.reversed();
    }

    public List<MessageResponseDTO> convertToMessageDTO(List<Message> messages, String username){
        List<MessageResponseDTO> messageResponseDTOS = new ArrayList<>();

        for(Message message: messages){
            MessageResponseDTO messageResponseDTO = new MessageResponseDTO(message.getMessageText(), message.getOwner(), message.getHasBeenRead());
            messageResponseDTO.setDate(addZeroToTime(message.getSendingTime().getHours())+":"+addZeroToTime(message.getSendingTime().getMinutes()));
            if(message.getOwner().equals(username)){
                messageResponseDTO.setUserIsOwner(true);
            }
            messageResponseDTOS.add(messageResponseDTO);
        }

        return messageResponseDTOS;
    }

    public <T> T map(Object obj, Class<T> clazz){
        if(clazz == InfoOfMessageByBotDTO.class){
            Message message = (Message) obj;
            InfoOfMessageByBotDTO infoOfMessage = new InfoOfMessageByBotDTO(message.getChat().getId(), message.getMessageText());
            return (T) infoOfMessage;
        }
        return null;
    }

    private String addZeroToTime(int time){
        return time<10? "0"+time : time+"";
    }


    private Date nextDay(Date beginDate) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(beginDate);

        calendar.add(Calendar.DAY_OF_MONTH,1);
        return calendar.getTime();
    }

    private boolean equalsTwoDate(Date date1, Date date2){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date1OfString = simpleDateFormat.format(date1);
        String date2OfString = simpleDateFormat.format(date2);

        return date1OfString.equals(date2OfString);
    }

    private List<Message> deleteAddedMessages(List<Message> allMessages, List<Message> addedMessages){
        addedMessages.forEach(allMessages::remove);

        return allMessages;
    }

    private List<Message> getMessagesOfDate(List<Message> messages, Date date){
        List<Message> messagesReturn = new LinkedList<>();
        for(Message message: messages){
            if(equalsTwoDate(message.getSendingTime(), date)){
                messagesReturn.add(message);
            }
        }
        return messagesReturn;
    }

    private <T> T map(MessengerUser user, Class<T> clazz){
        if(clazz.equals(User.class)){
            return (T) userService.findById(user.getId());
        }else{
            return (T) botService.findById(user.getId());
        }
    }
}
