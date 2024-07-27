package com.example.Messenger.models.user;

import com.example.Messenger.util.enums.LanguageType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "Settings_of_user")
public class SettingsOfUser implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Enumerated(EnumType.STRING)
    private LanguageType lang;
    @Column(name = "translate_message_mode")
    private String translateMessageMode;
    @OneToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    @JsonIgnore
    private User owner;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LanguageType getLang() {
        return lang;
    }

    public void setLang(LanguageType lang) {
        this.lang = lang;
    }

    public String getTranslateMessageMode() {
        return translateMessageMode;
    }

    public void setTranslateMessageMode(String translateMessageMode) {
        this.translateMessageMode = translateMessageMode;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
