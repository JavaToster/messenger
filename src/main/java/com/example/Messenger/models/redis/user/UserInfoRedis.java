package com.example.Messenger.models.redis.user;

import com.example.Messenger.models.database.user.User;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@Data
public class UserInfoRedis implements Serializable {
    private int id;
    private String username;
    private String name;
    private String lastname;
    private String email;
    private String iconOfUserUrl;

    public UserInfoRedis(User user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.lastname = user.getLastname();
        this.email = user.getEmail();
        this.iconOfUserUrl = user.getLinkOfIcon();
    }
}
