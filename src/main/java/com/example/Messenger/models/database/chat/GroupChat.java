package com.example.Messenger.models.database.chat;

import jakarta.persistence.*;

@Entity
@Table(name = "Group_chat")
public class GroupChat extends Chat{
    @Column(name = "group_name")
    private String groupName;

    public GroupChat(){}

    public GroupChat(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "GroupChat{" +
                "groupName='" + groupName + '\'' +
                ", members=" + members +
                '}';
    }
}
