package com.example.Messenger.dto.chatHead.channel;

public class ChannelMemberDTO {
    private int id;
    private String username;
    private boolean block;

    public ChannelMemberDTO(){}

    public ChannelMemberDTO(int id, String username, boolean block){
        this.id = id;
        this.username = username;
        this.block = block;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }
}
