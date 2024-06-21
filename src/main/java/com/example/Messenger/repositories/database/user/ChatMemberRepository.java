package com.example.Messenger.repositories.database.user;

import com.example.Messenger.models.database.chat.Chat;
import com.example.Messenger.models.database.user.ChatMember;
import com.example.Messenger.models.database.user.MessengerUser;
import com.example.Messenger.models.database.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, Integer> {
    List<ChatMember> findByUser(MessengerUser user);

    Optional<ChatMember> findByUserAndChat(User user, Chat chat);
}
