package com.example.Messenger.dto.chatHead.channel;

import com.example.Messenger.dto.chatHead.ChatHeadDTO;
import com.example.Messenger.models.database.user.MessengerUser;

import java.util.List;

public class ChannelHeadDTO extends ChatHeadDTO {
    private String description;
    private MessengerUser owner;
    private MessengerUser nextOwner;
    private List<ChannelMemberDTO> members;
    private List<ChannelMemberDTO> admins;

    public ChannelHeadDTO(){}

    public ChannelHeadDTO(String name, int membersCount, String description, MessengerUser owner, List<ChannelMemberDTO> members,
                          List<ChannelMemberDTO> admins){
        this.name = name;
        this.description = description;
        this.members = members;
        this.admins = admins;
        this.underName = membersCount(membersCount);
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ChannelMemberDTO> getMembers() {
        return members;
    }

    public void setMembers(List<ChannelMemberDTO> member) {
        this.members = member;
    }

    public List<ChannelMemberDTO> getAdmins() {
        return admins;
    }

    public void setAdmins(List<ChannelMemberDTO> admins) {
        this.admins = admins;
    }

    private String membersCount(int count){
        return (count % 10)<5 ? count+" подписчик" : count+" подписчиков";
    }

    public MessengerUser getOwner() {
        return owner;
    }

    public void setOwner(MessengerUser owner) {
        this.owner = owner;
    }

    public MessengerUser getNextOwner() {
        return nextOwner;
    }

    public void setNextOwner(MessengerUser nextOwner) {
        this.nextOwner = nextOwner;
    }
}
