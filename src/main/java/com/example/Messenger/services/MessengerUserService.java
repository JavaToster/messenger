package com.example.Messenger.services;

import com.example.Messenger.models.MessengerUser;
import com.example.Messenger.repositories.MessengerUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MessengerUserService {
    private MessengerUserRepository messengerUserRepository;

    @Autowired
    public MessengerUserService(MessengerUserRepository messengerUserRepository) {
        this.messengerUserRepository = messengerUserRepository;
    }

    public MessengerUser findById(int id) {
        return messengerUserRepository.findById(id).orElse(null);
    }

    public List<MessengerUser> findWithout(String username){
        List<MessengerUser> users = messengerUserRepository.findAll();
        users.removeIf(user -> user.getUsername().equals(username));
        return users;
    }

    public MessengerUser findByUsername(String username) {
        return messengerUserRepository.findByUsername(username).orElse(null);
    }
}
