package com.example.Messenger.util.balancer;

import com.example.Messenger.models.user.User;
import com.example.Messenger.util.enums.UserStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserStatusBalancer {
    private final Map<String, UserStatus> userStatusByMailMap = new HashMap<>();
    private final Map<String, UserStatus> userStatusByUsernameMap = new HashMap<>();

    public void addUserByEmail(String email){
        userStatusByMailMap.put(email, UserStatus.NOTHING);
    }

    public void addUserByEmail(String email, UserStatus status){
        userStatusByMailMap.put(email, status);
    }

    public void removeUserFromEmail(String email){
        userStatusByMailMap.remove(email);
    }

    public boolean checkStatusByEmail(String email, UserStatus status){
        return userStatusByMailMap.get(email) == status;
    }

    public void setStatusByEmail(String email, UserStatus status){
        userStatusByMailMap.put(email, status);
    }

//    public void setStatusByUsername(String username, UserStatus status){
//        userStatusByUsernameMap.put(username, status);
//    }
//
//    public boolean checkStatusByUsername(String username, UserStatus status){
//        return userStatusByUsernameMap.get(username) ==  status;
//    }
}
