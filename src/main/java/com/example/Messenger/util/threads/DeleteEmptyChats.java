package com.example.Messenger.util.threads;

import com.example.Messenger.models.Chat;
import com.example.Messenger.services.ChatService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class DeleteEmptyChats extends Thread{

    private final ChatService chatService;

    @Autowired
    public DeleteEmptyChats(ChatService chatService) {
        this.chatService = chatService;
    }


    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(1000*120);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("I worked!");
            chatService.deleteEmptyChats();
        }
    }
}
