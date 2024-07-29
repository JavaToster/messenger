package com.example.Messenger.services.database.message;

import com.example.Messenger.models.message.BlockMessage;
import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.repositories.database.message.BlockMessageRepository;
import com.example.Messenger.repositories.database.chat.ChatRepository;
import com.example.Messenger.util.exceptions.ChatNotFoundException;
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
    public void add(BlockMessage blockMessage,int chatId) {
        if(!isBlockMessage(blockMessage.getContent(), chatId)){
            blockMessage.setChat(chatRepository.findById(chatId).orElseThrow(ChatNotFoundException::new));
            blockMessageRepository.save(blockMessage);
        }
    }

    public List<BlockMessage> findByChat(int chatId) {
        return blockMessageRepository.findByChat(chatRepository.findById(chatId).orElse(null));
    }

    @Transactional
    public void remove(long messageId) {
        blockMessageRepository.deleteById(messageId);
    }
    
    public boolean isBlockMessage(String content, int chatId){
        List<BlockMessage> blockMessages = blockMessageRepository.findByChat(chatRepository.findById(chatId).orElse(null));
        for(BlockMessage blockMessage: blockMessages){
            if(content.equalsIgnoreCase(blockMessage.getContent())){
                return true;
            }
        }
        return false;
    }

    public boolean contentIsBlocked(String content, int chatId){
        List<BlockMessage> blockMessagesOfChat = chatRepository.findById(chatId).orElseThrow(ChatNotFoundException::new).getBlockMessages();
        for(BlockMessage blockMessage: blockMessagesOfChat){
            if(blockMessage.equalsContent(content)){
                return true;
            }
        }
        return false;
    }
}
