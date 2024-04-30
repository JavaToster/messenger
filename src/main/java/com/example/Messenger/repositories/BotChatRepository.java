package com.example.Messenger.repositories;

import com.example.Messenger.models.BotChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotChatRepository extends JpaRepository<BotChat, Integer> {
}
