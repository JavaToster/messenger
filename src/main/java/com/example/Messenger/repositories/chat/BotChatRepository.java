package com.example.Messenger.repositories.chat;

import com.example.Messenger.models.chat.BotChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotChatRepository extends JpaRepository<BotChat, Integer> {
}
