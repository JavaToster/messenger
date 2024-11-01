package com.example.Messenger.repositories.database.message;

import com.example.Messenger.models.message.LateMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LateMessageRepository extends JpaRepository<LateMessage, Long> {

}
