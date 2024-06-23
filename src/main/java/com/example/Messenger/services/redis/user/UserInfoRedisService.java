package com.example.Messenger.services.redis.user;

import com.example.Messenger.repositories.redis.user.UserInfoRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInfoRedisService {
    private final UserInfoRedisRepository userInfoRedisRepository;


}
