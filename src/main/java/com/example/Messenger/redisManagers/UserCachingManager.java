package com.example.Messenger.redisManagers;

import com.example.Messenger.dto.user.UserDTO;
import com.example.Messenger.models.user.IconOfUser;
import com.example.Messenger.models.user.User;
import com.example.Messenger.repositories.database.user.UserRepository;
import com.example.Messenger.util.Convertor;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCachingManager {
    private final Convertor convertor;

    @CachePut(value = "userInfoByUsername", key = "#result.username")
    public UserDTO setLinkOfUserIcon(String link, User user){
        IconOfUser icon = new IconOfUser(link, user);
        user.setIcon(icon);
        return convertor.convertToUserDTO(user);
    }
}
