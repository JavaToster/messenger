package com.example.Messenger.services.email;

import com.example.Messenger.balancers.RestoreByEmailBalancer;
import com.example.Messenger.util.Convertor;
import com.example.Messenger.util.enums.StatusOfEqualsCodes;
import com.example.Messenger.exceptions.redis.RestoreCodeNotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class SendRestoreCodeToEmailService {

    private final EmailService emailService;
    private final RestoreByEmailBalancer restoreEmailBalancer;
    private final Convertor convertor;

    public void sendCode(String email){
        String code = convertor.addZeroInStart(restoreEmailBalancer.getRestoreCode());

        emailService.send(email,
                "Restore code by TosterW messenger",
                "Your restore code is - "+code
        );

        try {
            restoreEmailBalancer.addEmail(email, code);
        }catch (RestoreCodeNotFoundException ignored) {}
    }

    public StatusOfEqualsCodes checkCode(String email, String code){
        return restoreEmailBalancer.checkCode(email, code);
    }

    // возвращает освободившийся код
    public void removeEmailFromBalancer(String email){
        Optional<String> optionalCode = restoreEmailBalancer.removeEmail(email);
        if(optionalCode.isEmpty()){
            return;
        }
        restoreEmailBalancer.returnCode(optionalCode.get());
    }
}
