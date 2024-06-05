package com.example.Messenger.util.threads;

import com.example.Messenger.services.chat.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteEmptyChatsThread extends Thread{
    private final ChatService chatService;

    @Autowired
    public DeleteEmptyChatsThread(ChatService chatService) {
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
