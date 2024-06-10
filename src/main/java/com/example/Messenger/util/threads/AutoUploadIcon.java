package com.example.Messenger.util.threads;

import com.example.Messenger.models.user.IconOfUser;
import com.example.Messenger.models.user.User;
import com.example.Messenger.repositories.user.IconOfUserRepository;
import com.example.Messenger.util.CloudinaryService;

import java.io.IOException;

public class AutoUploadIcon extends Thread{
    private final CloudinaryService cloudinaryService;
    private final String path;
    private final IconOfUserRepository iconOfUserRepository;
    private final User owner;

    public AutoUploadIcon(String path, User owner, CloudinaryService cloudinaryService, IconOfUserRepository iconOfUserRepository){
        this.path = path;
        this.owner = owner;
        this.cloudinaryService = cloudinaryService;
        this.iconOfUserRepository = iconOfUserRepository;
    }

    @Override
    public void run() {
        try {
            String link = cloudinaryService.sendIcon(path);
            iconOfUserRepository.save(new IconOfUser(link, owner));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
