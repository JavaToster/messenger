package com.example.Messenger.models.user;

import com.example.Messenger.models.message.Message;
import com.example.Messenger.util.enums.LanguageType;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "person")
public class User extends MessengerUser{
    @Column(name = "phone")
    private String phone;
    @Column(name = "firstname")
    private String name;
    @Column(name = "lastname")
    private String lastname;
    @Column(name = "password")
    private String password;
    @OneToMany(mappedBy = "user")
    private List<ChatMember> members;
    @OneToMany(mappedBy = "owner")
    private List<Message> messages;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastOnline")
    private Date lastOnline;
    @Enumerated(value = EnumType.STRING)
    private LanguageType lang;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername(){
        return this.username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public List<ChatMember> getMembers() {
        return members;
    }

    public void setMembers(List<ChatMember> members) {
        this.members = members;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(Date lastOnline) {
        this.lastOnline = lastOnline;
    }

    public boolean equals(User user){
        return this.username.equals(user.getUsername());
    }

    public boolean equals(String username){
        return this.username.equals(username);
    }

    public LanguageType getLang() {
        return lang;
    }

    public void setLang(LanguageType lang) {
        this.lang = lang;
    }
}
