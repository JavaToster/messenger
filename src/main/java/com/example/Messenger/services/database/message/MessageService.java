package com.example.Messenger.services.database.message;

import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.message.Message;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.models.user.User;
import com.example.Messenger.repositories.database.chat.ChatRepository;
import com.example.Messenger.repositories.database.message.BlockMessageRepository;
import com.example.Messenger.repositories.database.message.MessageRepository;
import com.example.Messenger.repositories.database.message.MessageWrapperRepository;
import com.example.Messenger.repositories.database.user.UserRepository;
import com.example.Messenger.services.database.chat.ChatService;
import com.example.Messenger.exceptions.chat.ChatNotFoundException;
import com.example.Messenger.exceptions.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final BlockMessageRepository blockMessageRepository;
    private final UserRepository userRepository;
    private final ChatService chatService;
    private final MessageWrapperRepository messageWrapperRepository;

    public List<Message> findByChat(int id) throws ChatNotFoundException {
        Chat chat = chatRepository.findById(id).orElseThrow(() -> new ChatNotFoundException("Chat not found"));
        return messageRepository.findByChat(chat).reversed();
    }

    public List<Message> findByChat(Chat chat) {
        return messageRepository.findByChat(chat).reversed();
    }

    public List<Message> findByUser(int userId){
        return messageRepository.findByOwner(userRepository.findById(userId).orElse(null));
    }

    public List<Message> findByUser(MessengerUser user){
        return messageRepository.findByOwner(user);
    }

    @Transactional
    public MessageWrapper sendTextMessage(int chatId, String username, String textOfMessage){
        User ownerOfMessage = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User with username '"+username+"' not found"));
        Chat chatOfMessage = chatRepository.findById(chatId).orElseThrow(() -> new ChatNotFoundException("Chat not found"));

        return new Message(textOfMessage, ownerOfMessage, chatOfMessage);
    }
}
