package com.example.Messenger.repositories;

import com.example.Messenger.models.Chat;
import com.example.Messenger.models.ChatMember;
import com.example.Messenger.models.MessengerUser;
import com.example.Messenger.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, Integer> {
    List<ChatMember> findByUser(MessengerUser user);

    Optional<ChatMember> findByUserAndChat(User user, Chat chat);
}
