package com.example.Messenger.util.threads;

import com.example.Messenger.models.user.User;
import com.example.Messenger.services.cloudinary.CloudinaryService;
import com.example.Messenger.services.email.redis.user.UserCachingService;

import java.util.Optional;

public class AutoUploadIcon extends Thread{
    private final CloudinaryService cloudinaryService;
    private final String path;
    private final User owner;
    private final UserCachingService userCachingService;
    public AutoUploadIcon(String path, User owner, CloudinaryService cloudinaryService, UserCachingService userCachingService){
        this.path = path;
        this.owner = owner;
        this.cloudinaryService = cloudinaryService;
        this.userCachingService = userCachingService;
    }

    @Override
    public void run() {
        Optional<String> optionalLink = cloudinaryService.sendIcon(path);
        if(optionalLink.isPresent()){
            userCachingService.setLinkOfUserIcon(optionalLink.get(), owner);
        }
    }
}
