package com.example.Messenger.services.user;

import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.user.ChatMember;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.repositories.chat.ChatRepository;
import com.example.Messenger.repositories.user.ChatMemberRepository;
import com.example.Messenger.repositories.user.MessengerUserRepository;
import com.example.Messenger.util.enums.ChatMemberType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MessengerUserService {
    private final MessengerUserRepository messengerUserRepository;
    private final ChatRepository chatRepository;
    private final ChatMemberRepository chatMemberRepository;

    @Autowired
    public MessengerUserService(MessengerUserRepository messengerUserRepository, ChatRepository chatRepository, ChatMemberRepository chatMemberRepository) {
        this.messengerUserRepository = messengerUserRepository;
        this.chatRepository = chatRepository;
        this.chatMemberRepository = chatMemberRepository;
    }

    public MessengerUser findById(int id) {
        return messengerUserRepository.findById(id).orElse(null);
    }

    public List<MessengerUser> findWithout(String username){
        List<MessengerUser> users = messengerUserRepository.findAll();
        users.removeIf(user -> user.getUsername().equals(username));
        return users;
    }

    public MessengerUser findByUsername(String username) {
        return messengerUserRepository.findByUsername(username).orElse(null);
    }

    @Transactional
    public void setAdmin(int userId, int channelId) {
        MessengerUser user = messengerUserRepository.findById(userId).orElse(null);
        Chat chat = chatRepository.findById(channelId).orElse(null);

        Optional<ChatMember> member = ChatMemberService.findByChatAndUser(user, chat);

        // если этот юзер не будет найдет в этом чате, то есть у него не будет chatMember в этом чате, то значит мы не будем ему назначить админство
        // поэтому тут стоит условие
        if(member.isPresent()){
            ChatMember newAdminMember = member.get();
            newAdminMember.setMemberType(ChatMemberType.ADMIN);
            chatMemberRepository.save(newAdminMember);
        }
    }

    @Transactional
    public void resetAdmin(int userId, int channelId) {
        MessengerUser user = messengerUserRepository.findById(userId).orElse(null);
        Chat chat = chatRepository.findById(channelId).orElse(null);

        // тут мы сразу получаем member потому что до этого он уже был назначем на должность админа, поэтому условие его проверки тут не требуется
        ChatMember member = ChatMemberService.findByChatAndUser(user, chat).get();

        System.out.println();

        member.setMemberType(ChatMemberType.MEMBER);
        chatMemberRepository.save(member);
    }
}
