package com.example.Messenger.services.database.chat;

import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.message.ContainerOfMessages;
import com.example.Messenger.models.user.ChatMember;
import com.example.Messenger.models.chat.GroupChat;
import com.example.Messenger.models.user.User;
import com.example.Messenger.repositories.database.user.ChatMemberRepository;
import com.example.Messenger.repositories.database.chat.GroupChatRepository;
import com.example.Messenger.util.enums.ChatMemberType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupChatService {

    private final GroupChatRepository groupChatRepository;
    private final ChatMemberRepository chatMemberRepository;

    @Transactional
    public int createNewGroupChat(List<User> members, String name, User owner){
        if (name == null || name.isEmpty()) {
            throw new NullPointerException("Group name can't be is empty");
        }

        if(members.isEmpty()){
            throw new NullPointerException("Group members can't be is 0");
        }

        int presentChatId = groupIsPresent(members, name, owner);

        if(presentChatId != -1){
            return presentChatId;
        }

        List<ChatMember> chatMembers = new ArrayList<>();;
        GroupChat groupChat = new GroupChat(name);
        ContainerOfMessages container = new ContainerOfMessages(1, groupChat);
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
        groupChat.setContainerOfMessages(List.of(container));

        chatMemberRepository.save(chatMember);
        return groupChatRepository.save(groupChat).getId();
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
