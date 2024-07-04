package com.example.Messenger.models.message;

import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.util.enums.MessageStatus;
import com.example.Messenger.util.enums.MessageType;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "Photo_message")
public class ImageMessage extends MessageWrapper{
    @Column(name = "text_under_photo")
    private String textUnderPhoto;
    @Column(name = "expansion")
    private String expansion;

    public ImageMessage(){}

    public ImageMessage(Chat chat, MessengerUser user, String url, String textUnderPhoto, String expansion){
        this.chat = chat;
        this.owner = user;
        this.sendingTime = new Date();
        this.hasBeenRead = MessageStatus.NOT_READ;
        this.content = url;
        this.textUnderPhoto = textUnderPhoto;
        this.expansion = expansion;
        this.type = MessageType.IMAGE;
    }

    public String getTextUnderPhoto() {
        return textUnderPhoto == null ? "" : textUnderPhoto;
    }

    public void setTextUnderPhoto(String textUnderPhoto) {
        this.textUnderPhoto = textUnderPhoto;
    }

    public String getExpansion() {
        return expansion;
    }

    public void setExpansion(String expansion) {
        this.expansion = expansion;
    }

    @Override
    public String getMessageSendingTime() {
        return "image";
    }
}
