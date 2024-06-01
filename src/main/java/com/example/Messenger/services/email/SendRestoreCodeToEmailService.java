package com.example.Messenger.services.email;

import com.example.Messenger.util.balancer.RestoreByEmailBalancer;
import com.example.Messenger.util.enums.StatusOfEqualsCodes;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class SendRestoreCodeToEmailService {

    private final EmailService emailService;
    private final RestoreByEmailBalancer restoreEmailBalancer;
    private final List<Integer> codesForRestore = new LinkedList<>();
    private final Random random = new Random();

    @PostConstruct
    public void initialize(){
        for(int i = 0; i<100_000; i++){
            codesForRestore.add(i);
        }
    }

    public void sendCode(String email){
        int indexOfCode = getIndexOfRestoreCode();
        int code = codesForRestore.get(indexOfCode);
        codesForRestore.remove(indexOfCode);

        emailService.send(email,
                "Restore code by TosterW messenger",
                "Your restore code is - "+checkZeroOfNumber(code)
        );

        restoreEmailBalancer.addEmail(email, code);
    }

    public StatusOfEqualsCodes checkCode(String email, int code){
        StatusOfEqualsCodes status = restoreEmailBalancer.checkCode(email, code);
        return status;
    }

    // возвращает освободившийся код
    public synchronized void removeEmailFromBalancer(String email){
        int code = restoreEmailBalancer.removeEmail(email);
        if(code == -1){
            return;
        }
        codesForRestore.add(code);
        System.out.println("email was removed");
    }

    private int getIndexOfRestoreCode(){
        return random.nextInt(codesForRestore.size());
    }

    // метод для проверки чисел от 1 до 10000
    // то есть если рандом выберет число 1, то мы должны возвращать 000001 и так далее
    private String checkZeroOfNumber(int number) {
        if(number >= 100000){
            return String.valueOf(number);
        }

        String zero = "";
        for(int i = 0 ; i< (6 - String.valueOf(number).length()) ; i++){
            zero += 0;
        }

        return zero+number;
    }
}
