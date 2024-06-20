package com.example.Messenger.repositories.message;

import com.example.Messenger.models.database.message.AnswerMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerMessageRepository extends JpaRepository<AnswerMessage, Integer> {

}
