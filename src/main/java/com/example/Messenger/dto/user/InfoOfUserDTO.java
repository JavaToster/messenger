package com.example.Messenger.dto.user;

import java.io.Serializable;
import java.util.List;

public class InfoOfUserDTO implements Serializable {
    private int id;
    private String username;
    private String lastTime;
    private String firstname;
    private String lastname;
    private String email;
    private List<String> imagesUrl;
    private String iconUrl;

    public InfoOfUserDTO(int id, String username, String firstname, String lastname, String email, String iconUrl) {
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.iconUrl = iconUrl;
    }

    public InfoOfUserDTO(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getImagesUrl() {
        return imagesUrl;
    }

    public void setImagesUrl(List<String> imagesUrl) {
        this.imagesUrl = imagesUrl;
    }

    public boolean isFirstname(){
        if(this.firstname != null && !this.firstname.isEmpty()){
            return true;
        }
        return false;
    }

    public boolean isLastname(){
        if(this.lastname != null && !this.lastname.isEmpty()){
            return true;
        }
        return false;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public boolean isIconOfUser(){
        if(iconUrl == null || iconUrl.isEmpty()){
            return false;
        }return true;
    }
}
