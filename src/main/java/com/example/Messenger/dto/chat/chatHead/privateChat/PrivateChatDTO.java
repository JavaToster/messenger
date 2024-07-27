package com.example.Messenger.dto.chat.channel.chatHead.privateChat;

import com.example.Messenger.dto.chat.channel.chatHead.ChatHeadDTO;

import java.util.Date;

public class PrivateChatDTO extends ChatHeadDTO {
    private String phone;

    public PrivateChatDTO(String interlocutorName, Date lastOnline, String phone){
        this.name = interlocutorName;
        this.phone = phone;
        this.underName = addZero(lastOnline.getHours())+":"+addZero(lastOnline.getMinutes());
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private String addZero(int time){
        return time<10?"0"+time:""+time;
    }
}
