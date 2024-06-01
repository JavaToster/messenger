package com.example.Messenger.util.threads;

import com.example.Messenger.services.email.SendRestoreCodeToEmailService;

public class DeleteRestoreCode extends Thread{
    private volatile SendRestoreCodeToEmailService sendEmailService;
    private final String email;

    public DeleteRestoreCode(String email, SendRestoreCodeToEmailService sendEmailService){
        this.email = email;
        this.sendEmailService = sendEmailService;
    }

    @Override
    public void run(){
        try {
            Thread.sleep(1000*120);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        sendEmailService.removeEmailFromBalancer(email);
    }
}
