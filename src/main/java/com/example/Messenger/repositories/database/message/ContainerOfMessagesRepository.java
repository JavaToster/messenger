package com.example.Messenger.repositories.database.message;

import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.message.ContainerOfMessages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContainerOfMessagesRepository extends JpaRepository<ContainerOfMessages, Long> {
    List<ContainerOfMessages> findByChat(Chat chat);

    Optional<ContainerOfMessages> findByChatAndIdInChat(Chat chat, long idInChat);
}
