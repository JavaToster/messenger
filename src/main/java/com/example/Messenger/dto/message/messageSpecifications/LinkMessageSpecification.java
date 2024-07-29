package com.example.Messenger.dto.message.messageSpecifications;

import com.example.Messenger.util.abstractClasses.MessageSpecification;

public class LinkMessageSpecification extends MessageSpecification {
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
