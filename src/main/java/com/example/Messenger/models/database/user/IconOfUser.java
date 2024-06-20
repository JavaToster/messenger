package com.example.Messenger.models.database.user;

import jakarta.persistence.*;

@Entity
@Table(name = "Icon_of_user")
public class IconOfUser {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "link")
    private String link;
    @OneToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    public IconOfUser(){}
    public IconOfUser(String link, User owner){
        this.link = link;
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
