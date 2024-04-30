package com.example.Messenger.dto.util;

import com.example.Messenger.dto.message.MessageResponseDTO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DateDayOfMessagesDTO {
    private Date date;
    private List<MessageResponseDTO> messages;

    public DateDayOfMessagesDTO() {
    }

    public DateDayOfMessagesDTO(Date date, List<MessageResponseDTO> messages) {
        this.messages = messages;
        this.date = date;
    }

    public String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(this.date);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<MessageResponseDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageResponseDTO> messages) {
        this.messages = messages;
    }
}
