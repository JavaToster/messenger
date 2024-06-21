package com.example.Messenger.repositories.database.user;

import com.example.Messenger.models.database.user.MessengerUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessengerUserRepository extends JpaRepository<MessengerUser, Integer> {
    Optional<MessengerUser> findByUsername(String username);
}
