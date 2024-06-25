package com.example.Messenger.repositories.database.message;

import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.message.MessageWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageWrapperRepository extends JpaRepository<MessageWrapper, Integer> {
    List<MessageWrapper> findByChat(Chat chat);
}
