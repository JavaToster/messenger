package com.example.Messenger.repositories.message;

import com.example.Messenger.models.database.chat.Chat;
import com.example.Messenger.models.database.message.MessageWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageWrapperRepository extends JpaRepository<MessageWrapper, Integer> {
    List<MessageWrapper> findByChat(Chat chat);
}
