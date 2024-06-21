package com.example.Messenger.repositories.database.message;

import com.example.Messenger.models.database.message.ForwardMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForwardMessageRepository extends JpaRepository<ForwardMessage, Integer> {

}
