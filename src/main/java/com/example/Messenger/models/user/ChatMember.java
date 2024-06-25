package com.example.Messenger.models.user;

import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.util.enums.ChatMemberType;
import jakarta.persistence.*;

import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "chat_member")
public class ChatMember {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private MessengerUser user;
    @ManyToOne
    @JoinColumn(name = "chat_id", referencedColumnName = "id")
    private Chat chat;
    @Enumerated(value = EnumType.STRING)
    private ChatMemberType memberType;

    public ChatMember() {
    }

    public ChatMember(MessengerUser user, Chat chat, ChatMemberType memberType) {
        this.user = user;
        this.chat = chat;
        this.memberType = memberType;
    }

    public static List<Chat> getChatsOfUser(User member) {
        List<ChatMember> members = member.getMembers();
        List<Chat> chatsOfUser = new LinkedList<>();
        members.forEach(m -> chatsOfUser.add(m.getChat()));
        return chatsOfUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MessengerUser getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public ChatMemberType getMemberType() {
        return memberType;
    }

    public void setMemberType(ChatMemberType memberType) {
        this.memberType = memberType;
    }

    public String getUsernameOfUser(){
        return this.user.getUsername();
    }
}
