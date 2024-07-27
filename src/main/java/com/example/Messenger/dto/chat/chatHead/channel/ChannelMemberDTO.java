package com.example.Messenger.dto.chat.channel.chatHead.channel;

public class ChannelMemberDTO {
    private int id;
    private String username;
    private boolean block;
    private boolean admin;

    public ChannelMemberDTO(){}

    public ChannelMemberDTO(int id, String username, boolean block, boolean admin){
        this.id = id;
        this.username = username;
        this.block = block;
        this.admin = admin;
    }

    public ChannelMemberDTO(int id, String username){
        this.id = id;
        this.username = username;
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

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
