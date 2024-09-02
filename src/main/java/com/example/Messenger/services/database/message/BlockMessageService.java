package com.example.Messenger.services.database.message;

import com.example.Messenger.DAO.chat.ChatDAO;
import com.example.Messenger.DAO.message.BlockMessageDAO;
import com.example.Messenger.dto.message.NewBlockMessageDTO;
import com.example.Messenger.exceptions.message.BlockMessageValidateException;
import com.example.Messenger.models.message.BlockMessage;
import com.example.Messenger.repositories.database.message.BlockMessageRepository;
import com.example.Messenger.repositories.database.chat.ChatRepository;
import com.example.Messenger.exceptions.chat.ChatNotFoundException;
import com.example.Messenger.util.Convertor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BlockMessageService {
    private final BlockMessageDAO blockMessageDAO;
    private final ChatDAO chatDAO;
    private final Convertor convertor;

    @Transactional
    public void add(NewBlockMessageDTO newBlockMessage, int chatId, BindingResult errors) {
        if(errors.hasErrors()){
            List<String> errorsList = new ArrayList<>();
            errors.getAllErrors().forEach(error -> errorsList.add(error.getDefaultMessage()));
            throw new BlockMessageValidateException(String.join(";", errorsList));
        }

        if(!isBlockMessage(newBlockMessage, chatId)){
            BlockMessage blockMessage = convertor.convertToBlockMessage(newBlockMessage);
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
    
    public boolean isBlockMessage(NewBlockMessageDTO message, int chatId){
        List<BlockMessage> blockMessages = blockMessageDAO.findByChat(chatId);
        for(BlockMessage blockMessage: blockMessages){
            if(message.getContent().equalsIgnoreCase(blockMessage.getContent())){
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
