package com.example.Messenger.repositories;

import com.example.Messenger.models.Chat;
import com.example.Messenger.models.Message;
import com.example.Messenger.models.MessengerUser;
import com.example.Messenger.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByChat(Chat chat);
    List<Message> findByOwner(MessengerUser owner);
}
