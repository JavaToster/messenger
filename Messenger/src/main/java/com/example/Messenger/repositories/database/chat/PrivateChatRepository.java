package com.example.Messenger.repositories.database.chat;

import com.example.Messenger.models.chat.PrivateChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//@Repository
//public class PrivateChatRepository implements ChatRepository<PrivateChat> {
//
//    private final EntityManager entityManager;
//
//    @Autowired
//    public PrivateChatRepository(EntityManager entityManager) {
//        this.entityManager = entityManager;
//    }
//
//    @Override
//    public Optional<PrivateChat> findById(int id) {
//        return Optional.of(entityManager.find(PrivateChat.class, id));
//    }
//
//    @Override
//    public void save(PrivateChat chat) {
//        if(chat.getId() == 0){
//            entityManager.persist(chat);
//        }
//        entityManager.merge(chat);
//    }
//
//}

@Repository
public interface PrivateChatRepository extends JpaRepository<PrivateChat, Integer> {

}