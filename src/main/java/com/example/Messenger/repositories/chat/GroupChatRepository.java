package com.example.Messenger.repositories.chat;

import com.example.Messenger.models.chat.GroupChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//@Repository
//public class GroupChatRepository implements ChatRepository<GroupChat>{
//
//    private EntityManager entityManager;
//
//    @Autowired
//    public void setEntityManager(EntityManager entityManager) {
//        this.entityManager = entityManager;
//    }
//
//    @Override
//    public Optional<GroupChat> findById(int id) {
//        return Optional.of(entityManager.find(GroupChat.class, id));
//    }
//
//    @Override
//    public void save(GroupChat chat) {
//        if(chat.getId() == 0){
//            entityManager.persist(chat);
//            return;
//        }
//        entityManager.merge(chat);
//    }
//}

@Repository
public interface GroupChatRepository extends JpaRepository<GroupChat, Integer>{

}
