package com.example.Messenger.repositories.database.user;

import com.example.Messenger.models.user.ComplaintOfUser;
import com.example.Messenger.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintOfUserRepository extends JpaRepository<ComplaintOfUser, Integer> {
    List<ComplaintOfUser> findByOwner(User user);
}
