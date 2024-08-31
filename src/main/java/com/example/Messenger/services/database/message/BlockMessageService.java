package com.example.Messenger.services.database.message;

import com.example.Messenger.DAO.chat.ChatDAO;
import com.example.Messenger.DAO.message.BlockMessageDAO;
import com.example.Messenger.models.message.BlockMessage;
import com.example.Messenger.repositories.database.message.BlockMessageRepository;
import com.example.Messenger.repositories.database.chat.ChatRepository;
import com.example.Messenger.exceptions.chat.ChatNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BlockMessageService {
    private final BlockMessageDAO blockMessageDAO;
    private final ChatDAO chatDAO;

    @Transactional
    public void add(BlockMessage blockMessage,int chatId) {
        if(!isBlockMessage(blockMessage, chatId)){
            blockMessage.setChat(chatDAO.findById(chatId));
            blockMessageDAO.save(blockMessage);
        }
    }

    public List<BlockMessage> findByChat(int chatId) {
        return blockMessageDAO.findByChat(chatId);
    }

    @Transactional
    public void remove(long messageId) {
        blockMessageDAO.deleteById(messageId);
    }
    
    public boolean isBlockMessage(BlockMessage message, int chatId){
        List<BlockMessage> blockMessages = blockMessageDAO.findByChat(chatId);
        for(BlockMessage blockMessage: blockMessages){
            if(blockMessage.getContent().equalsIgnoreCase(blockMessage.getContent())){
                return true;
            }
        }
        return false;
    }

    public boolean contentIsBlocked(String content, int chatId){
        List<BlockMessage> blockMessagesOfChat = chatDAO.findById(chatId).getBlockMessages();
        for(BlockMessage blockMessage: blockMessagesOfChat){
            if(blockMessage.equalsContent(content)){
                return true;
            }
        }
        return false;
    }
}
