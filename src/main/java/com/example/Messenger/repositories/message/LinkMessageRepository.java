package com.example.Messenger.repositories.message;

import com.example.Messenger.models.message.LinkMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkMessageRepository extends JpaRepository<LinkMessage, Integer> {
}
