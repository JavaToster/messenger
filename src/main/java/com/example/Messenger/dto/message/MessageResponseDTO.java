package com.example.Messenger.dto.message;

import com.example.Messenger.models.database.message.MessageWrapper;
import com.example.Messenger.models.database.user.MessengerUser;
import com.example.Messenger.util.abstractClasses.UtilSpecification;
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
    private UtilSpecification specification;


    public MessageResponseDTO(MessageWrapper message, MessengerUser owner) {
        this.id = message.getId();
        this.message = message;
        this.owner = owner;
        this.type = message.typeToString();
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

    public UtilSpecification getSpecification() {
        return specification;
    }

    public void setSpecification(UtilSpecification specification) {
        this.specification = specification;
    }
}
