package com.example.Messenger.services.database.chat;

import com.example.Messenger.models.chat.Channel;
import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.user.ChatMember;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.models.user.User;
import com.example.Messenger.repositories.database.chat.ChannelRepository;
import com.example.Messenger.repositories.database.user.ChatMemberRepository;
import com.example.Messenger.repositories.database.user.MessengerUserRepository;
import com.example.Messenger.util.enums.ChatMemberType;
import com.example.Messenger.util.exceptions.ChatNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final MessengerUserRepository messengerUserRepository;

    @Transactional
    public int createNewChannel(List<User> subscribers, User channelOwner, String channelName){
        if(channelName == null || channelName.isEmpty()){
            throw new NullPointerException("Channel name can't be empty");
        }

        if(subscribers.isEmpty()){
            throw new NullPointerException("Channel subscribers can't be is 0");
        }

        int channelIsPresent = channelIsPresent(subscribers, channelOwner, channelName);
        if(channelIsPresent != -1){
            return channelIsPresent;
        }

        Channel channel = new Channel();
        List<ChatMember> chatMembers = new ArrayList<>();
        chatMembers.add(new ChatMember(channelOwner, channel, ChatMemberType.OWNER));
        for(User subscriber: subscribers){
            ChatMember chatMember =  new ChatMember(subscriber, channel, ChatMemberType.SUBSCRIBER);
            chatMembers.add(chatMember);
        }
        chatMembers.forEach(chatMemberRepository::save);
        channel.setMembers(chatMembers);
        channel.setName(channelName);
        return channelRepository.save(channel).getId();
    }

    public String getChannelName(int id) {
        return channelRepository.findById(id).orElse(null).getName();
    }

    public String getChannelName(Chat chat){
        return channelRepository.findById(chat.getId()).orElse(null).getName();
    }

    private int channelIsPresent(List<User> subscribers, User channelOwner, String channelName){
        List<Channel> channels = channelRepository.findAll();
        for(Channel channel: channels){
            ChatMember owner = getOwner(channel);
            channel.getMembers().remove(owner);
            List<User> users = new ArrayList<>();
            channel.getMembers().forEach(member -> users.add((User)member.getUser()));
            if(channel.getName().equals(channelName) &&  ((User)owner.getUser()).getUsername().equals(channelOwner.getUsername()) && users.equals(subscribers)){
                return channel.getId();
            }
        }
        return -1;
    }

    private ChatMember getOwner(Channel channel){
        List<ChatMember> members = channel.getMembers();
        for(ChatMember chatMember: members){
            if(chatMember.getMemberType() == ChatMemberType.OWNER){
                return chatMember;
            }
        }
        return null;
    }

    public User getChannelOwner(int chatId){
        Chat chat = channelRepository.findById(chatId).orElse(null);
        List<ChatMember> members = chat.getMembers();
        for(ChatMember member: members){
            if(member.getMemberType() == ChatMemberType.OWNER){
                return (User) member.getUser();
            }
        }
        return null;
    }

    public Chat findById(int chatId) {
        return channelRepository.findById(chatId).orElse(null);
    }

    public int countMembers(int chatId){
        return channelRepository.findById(chatId).orElse(null).getMembers().size();
    }

    public int countMembers(Chat chat){
        return channelRepository.findById(chat.getId()).orElse(null).getMembers().size();
    }

    public boolean isOwner(int chatId, String username) {
        return isOwner(chatId, messengerUserRepository.findByUsername(username).orElse(null));
    }

    public boolean isOwner(int chatId, int userId){
        return isOwner(chatId, messengerUserRepository.findById(userId).orElse(null));
    }

    private boolean isOwner(int chatId, MessengerUser user){
        List<ChatMember> members = channelRepository.findById(chatId).orElseThrow(ChatNotFoundException::new).getMembers();
        for(ChatMember member: members){
            if(member.getMemberType() == ChatMemberType.OWNER && member.getUser().equals(user.getUsername())){
                return true;
            }
        }
        return false;
    }

    public boolean isChannel(int toChatId) {
        return channelRepository.findById(toChatId).orElse(null) != null;
    }
}