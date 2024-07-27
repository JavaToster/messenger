package com.example.Messenger.repositories.database.message;

import com.example.Messenger.models.message.AnswerMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerMessageRepository extends JpaRepository<AnswerMessage, Integer> {

}
