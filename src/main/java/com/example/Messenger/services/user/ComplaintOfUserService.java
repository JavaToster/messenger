package com.example.Messenger.services.user;

import com.example.Messenger.models.user.ComplaintOfUser;
import com.example.Messenger.models.user.User;
import com.example.Messenger.repositories.user.ComplaintOfUserRepository;
import com.example.Messenger.repositories.user.UserRepository;
import com.example.Messenger.services.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ComplaintOfUserService {
    private final ComplaintOfUserRepository complaintOfUserRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Transactional
    public void addComplaint(String username, String complaintText, String fromUsername) {
        if(complaintText.isEmpty() || username.isEmpty()){
            return;
        }

        ComplaintOfUser complaint = new ComplaintOfUser(userRepository.findByUsername(username).orElse(null), complaintText, userRepository.findByUsername(fromUsername).orElse(null));
        complaintOfUserRepository.save(complaint);
    }

    public ComplaintOfUser getComplaintByUser(String username, int complaintIdByUser) {
        List<ComplaintOfUser> complaintsOfUser = userRepository.findByUsername(username).orElse(null).getComplaints();
        return complaintsOfUser.get(complaintIdByUser);
    }

    public ComplaintOfUser findComplaintById(int id){
        return complaintOfUserRepository.findById(id).orElse(null);
    }

    public List<ComplaintOfUser> findByUser(String username) {
        return complaintOfUserRepository.findByOwner(userRepository.findByUsername(username).orElse(null));
    }

    public void sendWarningEmail(String username) {
        String textOfWarning = "Hello, "+username+". We are obliged to inform you that complaints have been received against you," +
                " and we are forced to give you a warning. " +
                "Next time there will be a lockdown for 12 hours.";
        emailService.send(userRepository.findByUsername(username).orElse(null).getEmail(), "Warning", textOfWarning);
    }

    @Transactional
    public void removeComplaints(String username) {
        List<ComplaintOfUser> complaints = complaintOfUserRepository.findByOwner(getUser(username));
        complaints.forEach(complaint -> complaintOfUserRepository.delete(complaint));
    }

    private User getUser(String username){
        return userRepository.findByUsername(username).orElse(null);
    }
}
