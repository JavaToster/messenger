package com.example.Messenger.dto.chat.channel.chatHead.group;

import com.example.Messenger.dto.chat.channel.chatHead.ChatHeadDTO;

import java.util.List;

public class GroupChatDTO extends ChatHeadDTO {
    private String description;
    private List<GroupChatMemberDTO> members;

    public GroupChatDTO(String name, int membersCount, String description, List<GroupChatMemberDTO> members){
        this.name = name;
        this.description = description;
        this.members = members;
        this.underName = membersCount(membersCount);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<GroupChatMemberDTO> getMembers() {
        return members;
    }

    public void setMembers(List<GroupChatMemberDTO> members) {
        this.members = members;
    }

    private String membersCount(int count){
        return (count%10)<5 ? count + " участника": count+" участников";
    }
}
