package com.example.Messenger.util.threads;

import com.example.Messenger.models.database.user.IconOfUser;
import com.example.Messenger.models.database.user.User;
import com.example.Messenger.repositories.user.IconOfUserRepository;
import com.example.Messenger.services.cloudinary.CloudinaryService;

import java.util.Optional;

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
        Optional<String> optionalLink = cloudinaryService.sendIcon(path);
        if(optionalLink.isPresent()){
            String link = optionalLink.get();
            iconOfUserRepository.save(new IconOfUser(link, owner));
        }
    }
}
