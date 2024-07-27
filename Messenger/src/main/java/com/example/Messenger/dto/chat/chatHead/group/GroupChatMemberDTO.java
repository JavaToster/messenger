package com.example.Messenger.dto.chat.channel.chatHead.group;

import com.example.Messenger.util.enums.ChatMemberType;

public class GroupChatMemberDTO {
    private String username;
    private ChatMemberType memberType;

    public GroupChatMemberDTO(){}

    public GroupChatMemberDTO(String username, ChatMemberType memberType) {
        this.username = username;
        this.memberType = memberType;
    }

    public String getMemberType() {
        return memberType.name();
    }

    public void setMemberType(ChatMemberType memberType) {
        this.memberType = memberType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
