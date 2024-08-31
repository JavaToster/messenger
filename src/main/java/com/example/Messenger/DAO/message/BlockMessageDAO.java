package com.example.Messenger.DAO.message;

import com.example.Messenger.exceptions.chat.ChatNotFoundException;
import com.example.Messenger.exceptions.message.BlockMessageNotFoundException;
import com.example.Messenger.models.message.BlockMessage;
import com.example.Messenger.repositories.database.chat.ChatRepository;
import com.example.Messenger.repositories.database.message.BlockMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BlockMessageDAO {
    private final BlockMessageRepository blockMessageRepository;
    private final ChatRepository chatRepository;

    public void save(BlockMessage blockMessage){
        blockMessageRepository.save(blockMessage);
    }

    public BlockMessage findById(long id){
        return blockMessageRepository.findById(id).orElseThrow(BlockMessageNotFoundException::new);
    }

    public List<BlockMessage> findByChat(int chatId){
        return blockMessageRepository.findByChat(chatRepository.findById(chatId).orElseThrow(ChatNotFoundException::new));
    }

    public void deleteById(long messageId) {
        blockMessageRepository.deleteById(messageId);
    }
}
