package com.example.Messenger.repositories.database.message;

import com.example.Messenger.models.message.LateMessagesContainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LateMessagesContainerRepository extends JpaRepository<LateMessagesContainer, Long> {
}
