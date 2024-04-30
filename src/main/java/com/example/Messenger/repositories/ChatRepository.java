package com.example.Messenger.repositories;

import com.example.Messenger.models.Chat;
import com.example.Messenger.models.ChatMember;
import com.example.Messenger.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
    List<Chat> findByMembers(ChatMember user);
}
