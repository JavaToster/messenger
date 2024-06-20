package com.example.Messenger.models.database.user;

import jakarta.persistence.*;

import java.util.Date;

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
    @Column(name = "text")
//    @Size(min = 250, max = 500)
    private String text;
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;
    @ManyToOne
    @JoinColumn(name = "from_id", referencedColumnName = "id")
    private User from;

    public ComplaintOfUser(User owner, String text, User from){
        this.owner = owner;
        this.text = text;
        this.time = new Date();
        this.from = from;
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }
}
