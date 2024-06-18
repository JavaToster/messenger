package com.example.Messenger.util.threads;

import com.example.Messenger.services.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteEmptyChatsThread extends Thread{
    private final ChatService chatService;
    private final int TIME_OF_SLEEP = 1000*120;

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(TIME_OF_SLEEP);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("I worked!");
            chatService.deleteEmptyChats();
        }
    }
}
