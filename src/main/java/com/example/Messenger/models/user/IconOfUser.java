package com.example.Messenger.models.user;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "Icon_of_user")
public class IconOfUser implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "link")
    private String link;
    @OneToOne
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private com.example.Messenger.models.user.User owner;

    public IconOfUser(){}
    public IconOfUser(String link, com.example.Messenger.models.user.User owner){
        this.link = link;
        this.owner = owner;
    }
}
