package com.example.Messenger.dto.message.util;

import com.example.Messenger.util.abstractClasses.UtilSpecification;

public class ForwardMessageSpecification extends UtilSpecification {
    private String forwardMessageType;
    private String ownerFrom;

    /** using if message is image*/
    private String textUnderImage;
    public ForwardMessageSpecification(String forwardMessageType, String ownerFrom) {
        this.forwardMessageType = forwardMessageType;
        this.ownerFrom = ownerFrom;
    }

    public ForwardMessageSpecification(String forwardMessageType, String ownerFrom, String textUnderImage) {
        this.forwardMessageType = forwardMessageType;
        this.ownerFrom = ownerFrom;
        this.textUnderImage = textUnderImage;
    }

    public String getForwardMessageType() {
        return forwardMessageType;
    }

    public void setForwardMessageType(String forwardMessageType) {
        this.forwardMessageType = forwardMessageType;
    }

    public String getOwnerFrom() {
        return ownerFrom;
    }

    public void setOwnerFrom(String ownerFrom) {
        this.ownerFrom = ownerFrom;
    }

    public String getTextUnderImage() {
        return textUnderImage;
    }

    public void setTextUnderImage(String textUnderImage) {
        this.textUnderImage = textUnderImage;
    }
}
