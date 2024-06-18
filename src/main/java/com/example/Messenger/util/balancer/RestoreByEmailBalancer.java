package com.example.Messenger.util.balancer;

import com.example.Messenger.util.email.RestoreEmailsBox;
import com.example.Messenger.util.enums.StatusOfEqualsCodes;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RestoreByEmailBalancer {
    // здесь будут хранится коды от email'ов, одному email 1 код
    private final ConcurrentHashMap<String, RestoreEmailsBox> emailToCodeMap = new ConcurrentHashMap<>();
    public void addEmail(String email, int code){
        if(!emailToCodeMap.containsKey(email)){
            emailToCodeMap.put(email, new RestoreEmailsBox(Integer.valueOf(code)));
        }else{
            RestoreEmailsBox box = emailToCodeMap.get(email);
            box.setCode(code);
            emailToCodeMap.put(email, box);
        }
    }

    public int removeEmail(String email){
        int emptyCode;
        try {
            emptyCode = emailToCodeMap.get(email).getCode();
        }catch (NullPointerException e){
            return -1;
        }
        emailToCodeMap.remove(email);
        return emptyCode;
    }


    public StatusOfEqualsCodes checkCode(String email, int code) {
        RestoreEmailsBox box = emailToCodeMap.get(email);
        StatusOfEqualsCodes status = box.equalCodes(code);
        return status;
    }
}
