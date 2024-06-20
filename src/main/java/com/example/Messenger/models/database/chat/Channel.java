package com.example.Messenger.models.database.chat;

import com.example.Messenger.models.database.user.ChatMember;
import com.example.Messenger.models.database.user.MessengerUser;
import com.example.Messenger.util.enums.ChatMemberType;
import jakarta.persistence.*;

@Entity
@Table(name = "Channel")
public class Channel extends Chat{
    @Column(name = "Channel_name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MessengerUser getOwner(){
        for(ChatMember member: this.members){
            if(member.getMemberType() == ChatMemberType.OWNER){
                return member.getUser();
            }
        }
        return null;
    }
}
