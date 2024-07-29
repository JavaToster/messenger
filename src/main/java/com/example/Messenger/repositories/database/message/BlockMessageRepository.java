package com.example.Messenger.repositories.database.message;

import com.example.Messenger.models.message.BlockMessage;
import com.example.Messenger.models.chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockMessageRepository extends JpaRepository<BlockMessage, Long> {
    List<BlockMessage> findByChat(Chat chat);
}
