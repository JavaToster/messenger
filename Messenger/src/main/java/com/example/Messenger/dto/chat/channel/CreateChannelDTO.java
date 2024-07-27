package com.example.Messenger.dto.chat.channel;

import java.util.List;

public class CreateChannelDTO {
    private List<String> channelSubscribes;
    private String channelName;
    private String channelOwner;

    public List<String> getChannelSubscribes() {
        return channelSubscribes;
    }

    public void setChannelSubscribes(List<String> channelSubscribes) {
        this.channelSubscribes = channelSubscribes;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelOwner() {
        return channelOwner;
    }

    public void setChannelOwner(String channelOwner) {
        this.channelOwner = channelOwner;
    }
}
