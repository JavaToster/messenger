package com.example.Messenger.repositories;

import com.example.Messenger.models.BlockMessage;
import com.example.Messenger.models.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockMessageRepository extends JpaRepository<BlockMessage, Integer> {
    List<BlockMessage> findByChat(Chat chat);
}
