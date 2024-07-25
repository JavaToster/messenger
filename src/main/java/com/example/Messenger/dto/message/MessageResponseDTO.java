package com.example.Messenger.dto.message;

import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.util.abstractClasses.MessageSpecification;
import com.example.Messenger.util.enums.MessageStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class MessageResponseDTO {
    private int id;
    private MessageWrapper message;
    private String date;
    private MessengerUser owner;
    private String type;
    @JsonIgnore
    private boolean read;
    @JsonIgnore
    private boolean userIsOwner;
    //use only message with type of image
    private MessageSpecification specification;


    public MessageResponseDTO(MessageWrapper message) {
        this.id = message.getId();
        this.message = message;
        this.owner = message.getOwner();
        this.type = message.getType().name().toLowerCase();
        this.date = message.getMessageSendingTime();
        if(message.getHasBeenRead() == MessageStatus.READ){
            this.read = true;
            return;
        }
        this.read = false;
    }

    public MessageWrapper getMessage() {
        return message;
    }

    public void setMessage(MessageWrapper message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public MessengerUser getOwner() {
        return owner;
    }

    public void setOwner(MessengerUser owner) {
        this.owner = owner;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isUserIsOwner() {
        return userIsOwner;
    }

    public void setUserIsOwner(boolean userIsOwner) {
        this.userIsOwner = userIsOwner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MessageSpecification getSpecification() {
        return specification;
    }

    public void setSpecification(MessageSpecification specification) {
        this.specification = specification;
    }
}
