package com.example.Messenger.DAO.user;

import com.example.Messenger.models.user.ComplaintOfUser;
import com.example.Messenger.models.user.User;
import com.example.Messenger.repositories.database.user.ComplaintOfUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ComplaintOfUserDAO{
    private final ComplaintOfUserRepository complaintOfUserRepository;

    public List<ComplaintOfUser> findByOwner(User user) {
        return complaintOfUserRepository.findByOwner(user);
    }
}
