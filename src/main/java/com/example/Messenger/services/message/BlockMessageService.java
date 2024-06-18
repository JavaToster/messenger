package com.example.Messenger.services.message;

import com.example.Messenger.models.message.BlockMessage;
import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.repositories.message.BlockMessageRepository;
import com.example.Messenger.repositories.chat.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BlockMessageService {
    private final BlockMessageRepository blockMessageRepository;
    private final ChatRepository chatRepository;

    @Transactional
    public void add(BlockMessage blockMessage) {
        blockMessageRepository.save(blockMessage);
    }

    public List<BlockMessage> findByChat(int chatId) {
        return blockMessageRepository.findByChat(chatRepository.findById(chatId).orElse(null));
    }

    @Transactional
    public void remove(int messageId) {
        blockMessageRepository.deleteById(messageId);
    }

    public List<Chat> getChats(int chatId){
        List<Chat> chats = new ArrayList<>();
        blockMessageRepository.findByChat(chatRepository.findById(chatId).orElse(null)).forEach(blockMessage -> chats.add(blockMessage.getChat()));
        return chats;
    }
    
    public boolean isBlockMessage(BlockMessage message, int chatId){
        List<BlockMessage> blockMessages = blockMessageRepository.findByChat(chatRepository.findById(chatId).orElse(null));
        for(BlockMessage blockMessage: blockMessages){
            if(blockMessage.getText().equalsIgnoreCase(message.getText().toLowerCase())){
                return true;
            }
        }
        return false;
    }
}
