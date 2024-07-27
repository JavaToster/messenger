package com.example.Messenger.dto.message;

import com.example.Messenger.dto.ChatDTO;

public class FoundMessageDTO extends ChatDTO {
    private String beforeFoundWord;
    private String word;
    private String afterFoundWord;

    public String getBeforeFoundWord() {
        return beforeFoundWord;
    }

    public void setBeforeFoundWord(String beforeFoundWord) {
        this.beforeFoundWord = beforeFoundWord;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getAfterFoundWord() {
        return afterFoundWord;
    }

    public void setAfterFoundWord(String afterFoundWord) {
        this.afterFoundWord = afterFoundWord;
    }
}
