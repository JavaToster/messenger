package com.example.Messenger.repositories.chat;

import com.example.Messenger.models.database.chat.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Integer> {

}
