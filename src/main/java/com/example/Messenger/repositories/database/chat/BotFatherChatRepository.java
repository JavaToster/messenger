package com.example.Messenger.repositories.database.chat;

import com.example.Messenger.models.chat.BotFatherChat;
import com.example.Messenger.models.user.MessengerUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BotFatherChatRepository extends JpaRepository<BotFatherChat, Long> {
    Optional<BotFatherChat> findByMembers(MessengerUser member);
}
