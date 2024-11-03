package com.example.Messenger.balancers;

import com.example.Messenger.util.email.RestoreEmailsBox;
import com.example.Messenger.util.enums.StatusOfEqualsCodes;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class RestoreByEmailBalancer {
    private final ConcurrentHashMap<String, RestoreEmailsBox> emailToCodeMap = new ConcurrentHashMap<>();
    private final List<Integer> codesForRestore = new LinkedList<>();
    private final Random random = new Random();

    @PostConstruct
    public void initialize(){
        for(int i = 0; i<100_000; i++){
            codesForRestore.add(i);
        }
    }

    public void addEmail(String email, String code){
        if(!emailToCodeMap.containsKey(email)){
            emailToCodeMap.put(email, new RestoreEmailsBox(code));
        }else{
            RestoreEmailsBox box = emailToCodeMap.get(email);
            box.setCode(code);
            emailToCodeMap.put(email, box);
        }
    }
    public Optional<String> removeEmail(String email){
        RestoreEmailsBox box = emailToCodeMap.get(email);
        if(box == null){
            Optional.empty();
        }
        emailToCodeMap.remove(email);
        return Optional.of(box.getCode());
    }

    public StatusOfEqualsCodes checkCode(String email, String code) {
        RestoreEmailsBox box = emailToCodeMap.get(email);
        return box.equalCodes(code);
    }

    public int getRestoreCode(){
        int indexOfCode = getIndexOfRestoreCode();
        int code = codesForRestore.get(indexOfCode);
        codesForRestore.remove(indexOfCode);
        return code;
    }

    private int getIndexOfRestoreCode(){
        return random.nextInt(codesForRestore.size());
    }

    public void returnCode(String code) {
        codesForRestore.add(Integer.valueOf(code));
    }
}
