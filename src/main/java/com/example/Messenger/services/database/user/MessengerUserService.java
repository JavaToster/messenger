package com.example.Messenger.services.database.user;

import com.example.Messenger.models.database.chat.Chat;
import com.example.Messenger.models.database.user.ChatMember;
import com.example.Messenger.models.database.user.MessengerUser;
import com.example.Messenger.repositories.database.chat.ChatRepository;
import com.example.Messenger.repositories.database.user.ChatMemberRepository;
import com.example.Messenger.repositories.database.user.MessengerUserRepository;
import com.example.Messenger.util.enums.ChatMemberType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MessengerUserService {
    private final MessengerUserRepository messengerUserRepository;
    private final ChatRepository chatRepository;
    private final ChatMemberRepository chatMemberRepository;

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

    @Transactional(isolation = Isolation.READ_COMMITTED)
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

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetAdmin(int userId, int channelId) {
        MessengerUser user = messengerUserRepository.findById(userId).orElse(null);
        Chat chat = chatRepository.findById(channelId).orElse(null);

        // тут мы сразу получаем member потому что до этого он уже был назначем на должность админа, поэтому условие его проверки тут не требуется
        ChatMember member = ChatMemberService.findByChatAndUser(user, chat).get();

        member.setMemberType(ChatMemberType.MEMBER);
        chatMemberRepository.save(member);
    }
}
