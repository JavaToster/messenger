package com.example.Messenger.services.auth;

import com.example.Messenger.balancers.BalancerOfFoundChats;
import com.example.Messenger.services.database.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final BalancerOfFoundChats balancerOfFoundChats;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void setUserSpecification(String username){
        userService.setLastOnline(username);
        if(balancerOfFoundChats.checkFoundEmptyChatsOfUser(username)){
            balancerOfFoundChats.addNewUser(username);
        }
    }
}
