package com.example.Messenger.services.redis.email;

import com.example.Messenger.redisModel.email.RestoreEmailBoxRedis;
import com.example.Messenger.util.exceptions.redis.RestoreCodeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestoreEmailBoxRedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    @Cacheable(value = "emailRestoreCodeBox", key = "#emailAddress")
    public RestoreEmailBoxRedis getRestoreCode(String emailAddress) throws RestoreCodeNotFoundException{
        throw new RestoreCodeNotFoundException();
    }

    @CachePut(value = "emailRestoreCodeBox", key = "#result.emailAddress")
    public RestoreEmailBoxRedis save(RestoreEmailBoxRedis emailBox){
        return emailBox;
    }

    @CacheEvict(value = "emailRestoreCodeBox", key = "#emailAddress")
    public void remove(String emailAddress){}

    public boolean isEmailBox(String emailAddress){
        return redisTemplate.hasKey(createKey(emailAddress));
    }

    private String createKey(String emailAddress){
        return new StringBuilder("emailRestoreCodeBox::").append(emailAddress).toString();
    }
}
