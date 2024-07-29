package com.example.Messenger.models.message;

import com.example.Messenger.models.chat.Chat;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "Block_Message")
public class BlockMessage {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "content")
    private String content;
    @ManyToOne
    @JoinColumn(name = "chat_id", referencedColumnName = "id")
    private Chat chat;

    public boolean equalsContent(String content) {
        return this.content.equalsIgnoreCase(content);
    }
}
