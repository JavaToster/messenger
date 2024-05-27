package com.example.Messenger.dto.message.util;

import com.example.Messenger.util.abstractClasses.UtilSpecification;

public class ImageMessageSpecification extends UtilSpecification {
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
