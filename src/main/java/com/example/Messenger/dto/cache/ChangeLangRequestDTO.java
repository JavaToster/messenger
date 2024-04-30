package com.example.Messenger.dto.cache;

import lombok.Data;

public class ChangeLangRequestDTO {
    private int userId;
    private String lang;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
