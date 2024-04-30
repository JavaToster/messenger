package com.example.Messenger.services;

import com.example.Messenger.dto.ChatDTO;
import com.example.Messenger.dto.message.BlockMessageDTO;
import com.example.Messenger.models.BlockMessage;
import com.example.Messenger.models.Chat;
import com.example.Messenger.models.GroupChat;
import com.example.Messenger.models.PrivateChat;
import com.example.Messenger.repositories.BlockMessageRepository;
import com.example.Messenger.repositories.ChannelRepository;
import com.example.Messenger.repositories.ChatRepository;
import com.example.Messenger.repositories.GroupChatRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class BlockMessageService {
    private final BlockMessageRepository blockMessageRepository;
    private final ChatRepository chatRepository;
    private final ChatService chatService;
    private final GroupChatRepository groupChatRepository;
    private final ChannelRepository channelRepository;
    private final ModelMapper modelMapper;
    @Autowired
    public BlockMessageService(BlockMessageRepository blockMessageRepository, ChatRepository chatRepository, ChatService chatService, GroupChatRepository groupChatRepository, ChannelRepository channelRepository, ModelMapper modelMapper) {
        this.blockMessageRepository = blockMessageRepository;
        this.chatRepository = chatRepository;
        this.chatService = chatService;
        this.groupChatRepository = groupChatRepository;
        this.channelRepository = channelRepository;
        this.modelMapper = modelMapper;
    }


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

    public List<BlockMessageDTO> convertToChatDTO(List<BlockMessage> blockMessages, String username){
        List<BlockMessageDTO> blockMessageDTOList = new ArrayList<>();
        for(BlockMessage blockMessage: blockMessages){
            Chat chat = blockMessage.getChat();
            String title;
            if(chat.getClass() == PrivateChat.class){
                title = chatService.getInterlocutor(username, chat).getUsername();
            }else if(chat.getClass() == GroupChat.class){
                title = groupChatRepository.findById(chat.getId()).orElse(null).getGroupName();
            }else{
                title = channelRepository.findById(chat.getId()).orElse(null).getName();
            }
            BlockMessageDTO blockMessageDTO = modelMapper.map(blockMessage, BlockMessageDTO.class);
            blockMessageDTO.setChatTitle(title);

            blockMessageDTOList.add(blockMessageDTO);
        }
        return blockMessageDTOList;
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
