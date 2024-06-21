package com.example.Messenger.repositories.database.user;

import com.example.Messenger.models.database.user.SettingsOfUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsOfUserRepository extends JpaRepository<SettingsOfUser, Long> {
}
