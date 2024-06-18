package com.example.Messenger.util.threads;

import com.example.Messenger.models.user.ComplaintOfUser;
import com.example.Messenger.models.user.User;
import com.example.Messenger.services.user.ComplaintOfUserService;
import com.example.Messenger.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.DateUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CheckComplaintsOfUserThread extends Thread{
    private final UserService userService;
    private final int TIME_OF_SLEEP = 1000*60;

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(TIME_OF_SLEEP);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            userService.banComplaintsUser();
            System.out.println("I checked a complaints of users");
        }
    }
}
