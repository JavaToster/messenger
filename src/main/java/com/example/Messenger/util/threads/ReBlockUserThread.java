package com.example.Messenger.util.threads;

import com.example.Messenger.services.user.UserService;
import com.example.Messenger.models.user.User;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ReBlockUserThread extends Thread{
    private final UserService userService;
    private final User user;
    private Calendar willReBlockTime;

    public ReBlockUserThread(User user, UserService userService, Calendar calendar){
        this.user = user;
        this.userService = userService;
        this.willReBlockTime = calendar;
    }

    @Override
    public void run() {
        while (willReBlockTime != null){
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(new Date());
            if(calendar.getTime().getTime() > willReBlockTime.getTime().getTime()){
                userService.unban(user);
                break;
            }
        }
    }
}
