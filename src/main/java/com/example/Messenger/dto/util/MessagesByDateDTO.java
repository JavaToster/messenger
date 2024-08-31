package com.example.Messenger.dto.util;

import com.example.Messenger.dto.message.MessageWrapperDTO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessagesByDateDTO {
    private Date date;
    private List<MessageWrapperDTO> messages;

    public MessagesByDateDTO() {
    }

    public MessagesByDateDTO(Date date, List<MessageWrapperDTO> messages) {
        this.messages = messages.reversed();
        this.date = date;
    }

    public String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(this.date);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<MessageWrapperDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageWrapperDTO> messages) {
        this.messages = messages;
    }
}
