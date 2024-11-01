package com.example.Messenger.services.database.user;

import com.example.Messenger.DAO.chat.ChatDAO;
import com.example.Messenger.DAO.user.ChatMemberDAO;
import com.example.Messenger.DAO.user.MessengerUserDAO;
import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.user.ChatMember;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.repositories.database.chat.ChatRepository;
import com.example.Messenger.repositories.database.user.ChatMemberRepository;
import com.example.Messenger.repositories.database.user.MessengerUserRepository;
import com.example.Messenger.util.enums.ChatMemberType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MessengerUserService {
    private final ChatDAO chatDAO;
    private final MessengerUserDAO messengerUserDAO;
    private final ChatMemberDAO chatMemberDAO;

    public MessengerUser findById(int id) {
        return messengerUserDAO.findById(id);
    }

    public List<MessengerUser> findWithout(String username){
        List<MessengerUser> users = messengerUserDAO.findAll();
        users.removeIf(user -> user.getUsername().equals(username));
        return users;
    }

    public MessengerUser findByUsername(String username) {
        return messengerUserDAO.findByUsername(username);
    }
}
