package com.example.Messenger.services.email.redis.user;

import com.example.Messenger.dto.user.InfoOfUserDTO;
import com.example.Messenger.models.user.IconOfUser;
import com.example.Messenger.models.user.User;
import com.example.Messenger.repositories.database.user.UserRepository;
import com.example.Messenger.util.Convertor;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCachingService {

    private final UserRepository userRepository;

    @CachePut(value = "userInfoByUsername", key = "#result.username")
    public InfoOfUserDTO setLinkOfUserIcon(String link, User user){
        IconOfUser icon = new IconOfUser(link, user);
        user.setIcon(icon);
        return Convertor.convertToInfoOfUser(user);
    }

    private User getUserByUsername(String username){
        return userRepository.findByUsername(username).orElse(null);
    }
}
