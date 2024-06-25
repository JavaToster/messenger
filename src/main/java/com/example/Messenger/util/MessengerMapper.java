package com.example.Messenger.util;

import com.example.Messenger.dto.bot.response.message.InfoByTextMessageDTO;
import com.example.Messenger.dto.chatHead.ChatHeadDTO;
import com.example.Messenger.dto.chatHead.botChat.BotChatHeadDTO;
import com.example.Messenger.dto.chatHead.channel.ChannelHeadDTO;
import com.example.Messenger.dto.chatHead.channel.ChannelMemberDTO;
import com.example.Messenger.dto.chatHead.group.GroupChatDTO;
import com.example.Messenger.dto.chatHead.group.GroupChatMemberDTO;
import com.example.Messenger.dto.chatHead.privateChat.PrivateChatDTO;
import com.example.Messenger.models.chat.BotChat;
import com.example.Messenger.models.chat.Channel;
import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.message.Message;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.models.message.ImageMessage;
import com.example.Messenger.models.user.ChatMember;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.models.user.User;
import com.example.Messenger.repositories.database.chat.ChannelRepository;
import com.example.Messenger.repositories.database.message.MessageRepository;
import com.example.Messenger.repositories.database.message.MessageWrapperRepository;
import com.example.Messenger.repositories.database.message.PhotoMessageRepository;
import com.example.Messenger.services.database.chat.ChannelService;
import com.example.Messenger.services.database.chat.ChatService;
import com.example.Messenger.services.database.chat.GroupChatService;
import com.example.Messenger.services.database.user.BotService;
import com.example.Messenger.services.database.user.UserService;
import com.example.Messenger.util.enums.ChatMemberType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MessengerMapper {

    private final ChannelService channelService;
    private final GroupChatService groupChatService;
    private final UserService userService;
    private final BotService botService;
    private final MessageRepository messageRepository;
    private final PhotoMessageRepository photoMessageRepository;
    private final MessageWrapperRepository messageWrapperRepository;
    private final ChannelRepository channelRepository;

    @Autowired
    public MessengerMapper(ChannelService channelService, GroupChatService groupChatService, ChatService chatService, UserService userService, BotService botService, MessageRepository messageRepository, PhotoMessageRepository photoMessageRepository, MessageWrapperRepository messageWrapperRepository, ChannelRepository channelRepository) {
        this.channelService = channelService;
        this.groupChatService = groupChatService;this.userService = userService;
        this.botService = botService;
        this.messageRepository = messageRepository;
        this.photoMessageRepository = photoMessageRepository;
        this.messageWrapperRepository = messageWrapperRepository;
        this.channelRepository = channelRepository;
    }
    //for channelConvert
    private List<ChannelMemberDTO> convertToChannelMemberDTOOfSubscriber(List<ChatMember> members){
        List<ChannelMemberDTO> memberDTOList = new LinkedList<>();
        for(ChatMember member: members){
            if(member.getMemberType() == ChatMemberType.OWNER || member.getMemberType() == ChatMemberType.ADMIN){
                continue;
            }
            memberDTOList.add(new ChannelMemberDTO(member.getUser().getId(), member.getUsernameOfUser(), member.getMemberType() == ChatMemberType.BLOCK, member.getMemberType() == ChatMemberType.ADMIN));
        }
//        members.forEach(member -> memberDTOList.add(new ChannelMemberDTO(member.getUser().getId(), member.getUsernameOfUser(), member.getMemberType() == ChatMemberType.BLOCK, member.getMemberType() == ChatMemberType.ADMIN)));
        return memberDTOList;
    }

    private List<ChannelMemberDTO> convertToChannelMemberDTOOfAdmin(List<ChatMember> members){
        List<ChannelMemberDTO> memberDTOList = new LinkedList<>();
        for(ChatMember member: members){
            if(member.getMemberType() == ChatMemberType.ADMIN){
                memberDTOList.add(new ChannelMemberDTO(member.getUser().getId(), member.getUsernameOfUser()));
            }
        }
        System.out.println(memberDTOList.size());
        return memberDTOList;
    }

    @Deprecated
    public ChatHeadDTO map(Chat chat){
        List<ChatMember> members = chat.getMembers();
        if(chat.getClass() == Channel.class){
            return new ChannelHeadDTO(channelService.getChannelName(chat), channelService.countMembers(chat), "someDescription", channelService.getChannelOwner(chat.getId()), convertToChannelMemberDTOOfSubscriber(members), convertToChannelMemberDTOOfAdmin(members));
        }else if(chat.getClass() == BotChat.class){
            return new BotChatHeadDTO(botService.getBotName(chat), "Some description of bot");
        }else{
            return new GroupChatDTO(groupChatService.getGroupName(chat), groupChatService.membersCount(chat), "someDescription", convertToGroupMemberDTO(members));
        }
    }

    @Deprecated
    public ChatHeadDTO map(Chat chat, String username){
        return new PrivateChatDTO(getInterlocutor(username, chat).getUsername(), userService.findByUsername(username).getLastOnline(), "79393826388");
    }


    private List<GroupChatMemberDTO> convertToGroupMemberDTO(List<ChatMember> members){
        List<GroupChatMemberDTO> memberDTOList = new LinkedList<>();
        members.forEach(member -> memberDTOList.add(new GroupChatMemberDTO(member.getUsernameOfUser(), member.getMemberType())));
        return memberDTOList;
    }

    //for messages convert

    public <T> T map(Object obj, Class<T> clazz){
        if(clazz == InfoByTextMessageDTO.class){
            Message message = (Message) obj;
            InfoByTextMessageDTO infoOfMessage = new InfoByTextMessageDTO(message.getChat().getId(), message.getContent());
            return (T) infoOfMessage;
        }else if(clazz == Message.class && obj instanceof MessageWrapper){
            return (T) messageRepository.findById(((MessageWrapper) obj).getId()).orElse(null);
        }else if(clazz == ImageMessage.class && obj instanceof MessageWrapper){
            return (T) photoMessageRepository.findById(((MessageWrapper) obj).getId()).orElse(null);
        }
        throw new RuntimeException();
    }

    private List<MessageWrapper> deleteAddedMessages(List<MessageWrapper> allMessages, List<MessageWrapper> addedMessages){
        addedMessages.forEach(allMessages::remove);

        return allMessages;
    }

    private <T> T map(MessengerUser user, Class<T> clazz){
        if(clazz.equals(User.class)){
            return (T) userService.findById(user.getId());
        }else{
            return (T) botService.findById(user.getId());
        }
    }

    public List<MessageWrapper> convertIdiesToMessage(List<Integer> integers) {
        List<MessageWrapper> message = new LinkedList<>();
        for(Integer integer: integers){
            message.add(messageWrapperRepository.findById(integer).orElse(null));
        }
        return message;
    }

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
}
