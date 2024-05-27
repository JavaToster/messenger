package com.example.Messenger.dto.message.util;

import com.example.Messenger.util.abstractClasses.UtilSpecification;

public class LinkMessageSpecification extends UtilSpecification {
    private String link;

    public LinkMessageSpecification(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
