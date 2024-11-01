package com.example.Messenger.balancers;

import com.example.Messenger.util.email.RestoreEmailsBox;
import com.example.Messenger.util.enums.StatusOfEqualsCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class RestoreByEmailBalancer {
    // здесь будут хранится коды от email'ов, одному email 1 код
    private final ConcurrentHashMap<String, RestoreEmailsBox> emailToCodeMap = new ConcurrentHashMap<>();
    public void addEmail(String email, int code){
        if(!emailToCodeMap.containsKey(email)){
            emailToCodeMap.put(email, new RestoreEmailsBox(code));
        }else{
            RestoreEmailsBox box = emailToCodeMap.get(email);
            box.setCode(code);
            emailToCodeMap.put(email, box);
        }
    }
    public int removeEmail(String email){
        RestoreEmailsBox box = emailToCodeMap.get(email);
        if(box == null){
            return -1;
        }
        emailToCodeMap.remove(email);
        return box.getCode();
    }

    public StatusOfEqualsCodes checkCode(String email, int code) {
        RestoreEmailsBox box = emailToCodeMap.get(email);
        StatusOfEqualsCodes status = box.equalCodes(code);
        return status;
    }
}
