package com.example.Messenger.repositories.user;

import com.example.Messenger.models.database.user.ComplaintOfUser;
import com.example.Messenger.models.database.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintOfUserRepository extends JpaRepository<ComplaintOfUser, Integer> {
    List<ComplaintOfUser> findByOwner(User user);
}
