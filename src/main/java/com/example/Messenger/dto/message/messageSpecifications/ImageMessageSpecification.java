package com.example.Messenger.dto.message.messageSpecifications;

import com.example.Messenger.util.abstractClasses.MessageSpecification;

public class ImageMessageSpecification extends MessageSpecification {
    private String textUnderImage;

    public ImageMessageSpecification(String textUnderImage) {
        this.textUnderImage = textUnderImage;
    }

    public String getTextUnderImage() {
        return textUnderImage;
    }

    public void setTextUnderImage(String textUnderImage) {
        this.textUnderImage = textUnderImage;
    }
}
