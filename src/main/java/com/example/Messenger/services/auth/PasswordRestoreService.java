package com.example.Messenger.services.auth;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class PasswordRestoreService {
    private final Set<String> restoreEmails = new HashSet<>();

    public void add(String email){
        restoreEmails.add(email);
    }

    public void remove(String email){
        restoreEmails.remove(email);
    }

    public boolean hasEmail(String email) {
        return restoreEmails.contains(email);
    }
}
