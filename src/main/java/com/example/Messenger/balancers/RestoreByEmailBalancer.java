package com.example.Messenger.balancers;

import com.example.Messenger.redisModel.email.RestoreEmailBoxRedis;
import com.example.Messenger.services.redis.email.RestoreEmailBoxRedisService;
import com.example.Messenger.util.email.RestoreEmailsBox;
import com.example.Messenger.util.enums.StatusOfEqualsCodes;
import com.example.Messenger.util.exceptions.redis.RestoreCodeAttemptsMaxException;
import com.example.Messenger.util.exceptions.redis.RestoreCodeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class RestoreByEmailBalancer {
    // здесь будут хранится коды от email'ов, одному email 1 код
    private final RestoreEmailBoxRedisService restoreEmailBoxRedisService;
//    public void addEmail(String email, int code){
//        if(!emailToCodeMap.containsKey(email)){
//            emailToCodeMap.put(email, new RestoreEmailsBox(Integer.valueOf(code)));
//        }else{
//            RestoreEmailsBox box = emailToCodeMap.get(email);
//            box.setCode(code);
//            emailToCodeMap.put(email, box);
//        }
//    }
    public void addEmail(String email, int code) throws RestoreCodeNotFoundException {
        if(!restoreEmailBoxRedisService.isEmailBox(email)){
            RestoreEmailBoxRedis restoreCode = new RestoreEmailBoxRedis(email, code);
            restoreEmailBoxRedisService.save(restoreCode);
        }else{
            RestoreEmailBoxRedis box = restoreEmailBoxRedisService.getRestoreCode(email);
            box.setCode(code);
            restoreEmailBoxRedisService.save(box);
        }
    }
//    public int removeEmail(String email){
//        int emptyCode;
//        try {
//            emptyCode = emailToCodeMap.get(email).getCode();
//        }catch (NullPointerException e){
//            return -1;
//        }
//        emailToCodeMap.remove(email);
//        return emptyCode;
//    }

    public int removeEmail(String email){
        int emptyCode;
        try{
            emptyCode = restoreEmailBoxRedisService.getRestoreCode(email).getCode();
        }catch (NullPointerException e){
            return -1;
        }
        restoreEmailBoxRedisService.remove(email);
        return emptyCode;
    }

//    public StatusOfEqualsCodes checkCode(String email, int code) {
//        RestoreEmailsBox box = emailToCodeMap.get(email);
//        StatusOfEqualsCodes status = box.equalCodes(code);
//        return status;
//    }
    public StatusOfEqualsCodes checkCode(String email, int code){
        RestoreEmailBoxRedis box = restoreEmailBoxRedisService.getRestoreCode(email);
        if(box.getCode() == code){
            return StatusOfEqualsCodes.EQUAL;
        }else{
            try{
                box.plusAttempts();
                restoreEmailBoxRedisService.save(box);
                return StatusOfEqualsCodes.NOT_EQUAL;
            }catch (RestoreCodeAttemptsMaxException e){
                return StatusOfEqualsCodes.ATTEMPT_MAX;
            }
        }
    }
}
