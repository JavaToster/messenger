package com.example.Messenger.repositories.database.user;

import com.example.Messenger.models.user.Bot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BotRepository extends JpaRepository<Bot, Integer> {
    Optional<Bot> findByToken(String token);

    Optional<Bot> findByUsername(String username);
}
