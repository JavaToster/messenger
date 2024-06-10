package com.example.Messenger.models.user;

import com.example.Messenger.models.message.Message;
import com.example.Messenger.util.enums.LanguageType;
import com.example.Messenger.util.enums.RoleOfUser;
import jakarta.persistence.*;
import com.example.Messenger.dto.user.RegisterUserDTO;

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
    @Column(name = "email")
    private String email;
    @OneToMany(mappedBy = "user")
    private List<ChatMember> members;
    @OneToMany(mappedBy = "owner")
    private List<Message> messages;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastOnline")
    private Date lastOnline;
    @Enumerated(value = EnumType.STRING)
    private LanguageType lang;
    @Enumerated(value = EnumType.STRING)
    private RoleOfUser role;
    @OneToMany(mappedBy = "owner")
    private List<ComplaintOfUser> complaints;
    @Column(name = "image_link")
    private String imageLink;

    public User(){}
    public User(String firstName, String lastname, String username, String password, String email, String phone, String lang){
        this.name = firstName;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.lang = LanguageType.valueOf(lang);
        this.email = email;
        this.role = RoleOfUser.ROLE_USER;
    }

    public User(RegisterUserDTO registerDTO){
        this.name = registerDTO.getFirstname();
        this.lastname = registerDTO.getLastname();
        this.username = registerDTO.getUsername();
        this.password = registerDTO.getPassword();
        this.phone = registerDTO.getPhone();
        this.lang = LanguageType.valueOf(registerDTO.getLang());
        this.email = registerDTO.getEmail();
        this.role = RoleOfUser.ROLE_USER;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getRole(){
        return this.role.name();
    }

    public void setRole(RoleOfUser role){
        this.role = role;
    }

    public List<ComplaintOfUser> getComplaints() {
        return complaints;
    }

    public void setComplaints(List<ComplaintOfUser> complaints) {
        this.complaints = complaints;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
