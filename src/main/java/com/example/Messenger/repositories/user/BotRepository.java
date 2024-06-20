package com.example.Messenger.repositories.user;

import com.example.Messenger.models.database.user.Bot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BotRepository extends JpaRepository<Bot, Integer> {
    Optional<Bot> findByToken(String token);
}
