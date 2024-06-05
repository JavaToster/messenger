package com.example.Messenger.services.user;

import com.example.Messenger.models.user.ComplaintOfUser;
import com.example.Messenger.repositories.user.ComplaintOfUserRepository;
import com.example.Messenger.repositories.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ComplaintOfUserService {
    private final ComplaintOfUserRepository complaintOfUserRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addComplaint(String username, String complaintText) {
        if(complaintText.isEmpty() || username.isEmpty()){
            return;
        }

        ComplaintOfUser complaint = new ComplaintOfUser(userRepository.findByUsername(username).orElse(null), complaintText);
        complaintOfUserRepository.save(complaint);
    }
}
