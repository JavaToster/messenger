package com.example.Messenger.repositories;

import com.example.Messenger.models.GroupChat;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
