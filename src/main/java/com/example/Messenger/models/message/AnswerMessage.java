package com.example.Messenger.models.message;

import jakarta.persistence.*;

@Entity
@Table(name = "Answer_message")
public class AnswerMessage extends MessageWrapper{
    @OneToOne
    @JoinColumn(name = "to_answer_message_id")
    private MessageWrapper toAnswerMessage;

    public MessageWrapper getToAnswerMessage() {
        return toAnswerMessage;
    }

    public void setToAnswerMessage(MessageWrapper toAnswerMessage) {
        this.toAnswerMessage = toAnswerMessage;
    }
}
