package com.example.Messenger.repositories.database.message;

import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.models.message.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByChat(Chat chat);
    List<Message> findByOwner(MessengerUser owner);
}
