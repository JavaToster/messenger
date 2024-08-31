package com.example.Messenger.DAO.user;

import com.example.Messenger.models.user.User;
import com.example.Messenger.repositories.database.user.UserRepository;
import com.example.Messenger.exceptions.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserDAO {
    private final UserRepository userRepository;

    public User findById(int id){
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public User findByUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public boolean isUserByUsername(String username){
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean isUserByEmail(String email){
        return userRepository.findByEmail(email).isPresent();
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }
}
