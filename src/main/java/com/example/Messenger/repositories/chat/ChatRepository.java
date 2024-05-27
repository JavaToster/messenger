package com.example.Messenger.repositories.chat;

import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.user.ChatMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
    List<Chat> findByMembers(ChatMember user);
}
