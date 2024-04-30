package com.example.Messenger.repositories;

import com.example.Messenger.models.Bot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BotRepository extends JpaRepository<Bot, Integer> {
    Optional<Bot> findByToken(String token);
}
