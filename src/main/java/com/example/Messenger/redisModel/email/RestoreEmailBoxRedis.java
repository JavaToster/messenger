package com.example.Messenger.redisModel.email;

import com.example.Messenger.util.exceptions.redis.RestoreCodeAttemptsMaxException;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.Optional;

@Data
@RedisHash(timeToLive = 120)
public class RestoreEmailBoxRedis implements Serializable {
    @Id
    private long id;
    @Indexed
    private String emailAddress;
    private int code;
    private int attempts;

    public RestoreEmailBoxRedis(String emailAddress, int code){
        this.emailAddress = emailAddress;
        this.code = code;
    }

    public void plusAttempts() throws RestoreCodeAttemptsMaxException{
        if(this.attempts > 4){
            throw new RestoreCodeAttemptsMaxException();
        }
        this.attempts += 1;
    }
}
