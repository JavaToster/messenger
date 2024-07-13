package com.example.Messenger.models.user;

import com.example.Messenger.models.message.Message;
import com.example.Messenger.util.enums.LanguageType;
import com.example.Messenger.util.enums.RoleOfUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import com.example.Messenger.dto.user.RegisterUserDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.spi.CalendarDataProvider;

@Entity
@Table(name = "person")
@Data
public class User extends MessengerUser implements Serializable {
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
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastOnline")
    @JsonIgnore
    private Date lastOnlineTime;
    @Enumerated(value = EnumType.STRING)
    @JsonIgnore
    private RoleOfUser role;
    @OneToMany(mappedBy = "owner")
    @JsonIgnore
    private List<ComplaintOfUser> complaints;
    @OneToOne(mappedBy = "owner")
    private IconOfUser icon;
    @OneToOne(mappedBy = "owner")
    @JsonIgnore
    private SettingsOfUser settingsOfUser;

    public User(){}

    public User(String firstName, String lastname, String username, String password, String email, String phone, String lang){
        this.name = firstName;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.email = email;
        this.role = RoleOfUser.ROLE_USER;
        this.lastOnlineTime = new Date();

    }
    public User(RegisterUserDTO registerDTO){
        this.name = registerDTO.getFirstname();
        this.lastname = registerDTO.getLastname();
        this.username = registerDTO.getUsername();
        this.password = registerDTO.getPassword();
        this.phone = registerDTO.getPhone();
        this.email = registerDTO.getEmail();
        this.role = RoleOfUser.ROLE_USER;
        this.lastOnlineTime = new Date();
    }
    public String getRole(){
        return this.role.name();
    }

    public String getLinkOfIcon(){
        if(this.icon == null){
            return "";
        }
        return this.icon.getLink();
    }

    public boolean equals(User user){
        return equals(user.getUsername());
    }

    public boolean equals(String username){
        return this.username.equals(username);
    }

}
