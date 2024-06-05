package com.example.Messenger.models.user;

import jakarta.persistence.*;

@Entity
@Table(name = "Complaint")
public class ComplaintOfUser {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private User owner;
    private String text;

    public ComplaintOfUser(User owner, String text){
        this.owner = owner;
        this.text = text;
    }

    public ComplaintOfUser(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
