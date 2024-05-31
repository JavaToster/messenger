package com.example.Messenger.util.balancer;

import com.example.Messenger.util.email.RestoreEmailsBox;
import com.example.Messenger.util.enums.StatusOfEqualsCodes;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RestoreByEmailBalancer {
    // здесь будут хранится коды от email'ов, одному email 1 код
    private final Map<String, RestoreEmailsBox> emailToCodeMap = new HashMap<>();

    public void addEmail(String email, int code){
        if(!emailToCodeMap.containsKey(email)){
            emailToCodeMap.put(email, new RestoreEmailsBox(Integer.valueOf(code)));
        }
    }

    public int removeEmail(String email){
        int emptyCode = emailToCodeMap.get(email).getCode();
        emailToCodeMap.remove(email);
        return emptyCode;
    }


    public StatusOfEqualsCodes checkCode(String email, int code) {
        RestoreEmailsBox box = emailToCodeMap.get(email);
        StatusOfEqualsCodes status = box.equalCodes(code);
        return status;
    }
}
