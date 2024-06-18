package com.example.Messenger.util.threads;

import com.example.Messenger.services.email.SendRestoreCodeToEmailService;

public class DeleteRestoreCodeThread extends Thread{
    private volatile SendRestoreCodeToEmailService sendEmailService;
    private final String email;

    private final int TIME_OF_SLEEP = 1000*120;

    public DeleteRestoreCodeThread(String email, SendRestoreCodeToEmailService sendEmailService){
        this.email = email;
        this.sendEmailService = sendEmailService;
    }

    @Override
    public void run(){
        try {
            Thread.sleep(TIME_OF_SLEEP);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        sendEmailService.removeEmailFromBalancer(email);
    }
}
