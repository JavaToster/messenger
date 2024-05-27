package com.example.Messenger.repositories.user;

import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.user.ChatMember;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, Integer> {
    List<ChatMember> findByUser(MessengerUser user);

    Optional<ChatMember> findByUserAndChat(User user, Chat chat);
}
