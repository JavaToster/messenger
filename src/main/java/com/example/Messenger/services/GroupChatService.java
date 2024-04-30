package com.example.Messenger.services;

import com.example.Messenger.models.Chat;
import com.example.Messenger.models.ChatMember;
import com.example.Messenger.models.GroupChat;
import com.example.Messenger.models.User;
import com.example.Messenger.repositories.ChatMemberRepository;
import com.example.Messenger.repositories.GroupChatRepository;
import com.example.Messenger.repositories.UserRepository;
import com.example.Messenger.util.enums.ChatMemberType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class GroupChatService {

    private final GroupChatRepository groupChatRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final UserRepository userRepository;

    @Autowired
    public GroupChatService(GroupChatRepository groupChatRepository, ChatMemberRepository chatMemberRepository, UserRepository userRepository) {
        this.groupChatRepository = groupChatRepository;
        this.chatMemberRepository = chatMemberRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public int createNewGroupChat(List<User> members, String name, User owner){
        int presentChatId = groupIsPresent(members, name, owner);

        if(presentChatId != -1){
            return presentChatId;
        }

        List<ChatMember> chatMembers = new ArrayList<>();;
        GroupChat groupChat = new GroupChat(name);
        for(User member: members){
            ChatMember chatMember = new ChatMember();
            chatMember.setChat(groupChat);
            chatMember.setUser(member);
            chatMember.setMemberType(ChatMemberType.MEMBER);
            chatMembers.add(chatMember);
            chatMemberRepository.save(chatMember);
        }

        ChatMember chatMember = new ChatMember(owner, groupChat, ChatMemberType.OWNER);
        chatMembers.add(chatMember);

        chatMemberRepository.save(chatMember);
        return groupChatRepository.save(groupChat).getId();
    }

    public String getGroupName(int chatId){
        return groupChatRepository.findById(chatId).orElse(null).getGroupName();
    }
    public String getGroupName(Chat chat){
        return groupChatRepository.findById(chat.getId()).orElse(null).getGroupName();
    }
    public User getGroupOwner(int chatId){
        Chat chat = groupChatRepository.findById(chatId).orElse(null);
        List<ChatMember> members = chat.getMembers();
        for(ChatMember member: members){
            if(member.getMemberType() == ChatMemberType.OWNER){
                return (User)member.getUser();
            }
        }
        return null;
    }

    private int groupIsPresent(List<User> members, String groupName, User owner){
        List<GroupChat> groupChats = groupChatRepository.findAll();
        for(GroupChat groupChat: groupChats){
            ChatMember groupOwner = getOwner(groupChat);
            groupChat.getMembers().remove(groupOwner);
            List<User> users = new ArrayList<>();
            groupChat.getMembers().forEach(member -> users.add((User)member.getUser()));
            if(groupChat.getGroupName().equals(groupName) && ((User)groupOwner.getUser()).getUsername().equals(owner.getUsername()) && users.equals(members)){
                return groupChat.getId();
            }
        }
        return -1;
    }

    private ChatMember getOwner(GroupChat groupChat){
        List<ChatMember> members = groupChat.getMembers();
        for(ChatMember chatMember: members){
            if(chatMember.getMemberType() == ChatMemberType.OWNER){
                return chatMember;
            }
        }
        return null;
    }

    public int membersCount(Chat chat) {
        return groupChatRepository.findById(chat.getId()).orElse(null).getMembers().size();
    }
}
