package com.example.Messenger.services.security;

import com.example.Messenger.repositories.database.user.UserRepository;
import lombok.RequiredArgsConstructor;
import com.example.Messenger.security.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.Messenger.models.database.user.User;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username - может быть как никнеймом так и почтой
        // поэтому сначала проверяем человека по никнейму, потом если его не находим, то проверяем его по почте
        // если по итогу человек не находится, выбрасываем исключение

        Optional<User> user = userRepository.findByUsername(username);
        if(user.isEmpty()){
            user = userRepository.findByEmail(username);
            if(user.isEmpty()){
                throw new UsernameNotFoundException("User is not exist");
            }
        }
        return new com.example.Messenger.security.UserDetails(user.get());
    }
}