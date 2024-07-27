package com.example.Messenger.repositories.database.user;

import com.example.Messenger.models.user.IconOfUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IconOfUserRepository extends JpaRepository<IconOfUser, Integer> {
}
