package com.example.Messenger.util.threads;

import com.example.Messenger.models.user.ComplaintOfUser;
import com.example.Messenger.models.user.User;
import com.example.Messenger.services.user.ComplaintOfUserService;
import com.example.Messenger.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CheckComplaintsOfUserThread extends Thread{
    private final UserService userService;

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(1000 * 60);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            userService.banComplaintsUser();
            System.out.println("I checked a complaints of users");
        }
    }
}
