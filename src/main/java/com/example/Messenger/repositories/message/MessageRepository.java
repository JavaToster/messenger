package com.example.Messenger.repositories.message;

import com.example.Messenger.models.database.chat.Chat;
import com.example.Messenger.models.database.user.MessengerUser;
import com.example.Messenger.models.database.message.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByChat(Chat chat);
    List<Message> findByOwner(MessengerUser owner);
}
