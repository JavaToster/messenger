package com.example.Messenger.util.threads;

import com.example.Messenger.models.user.User;
import com.example.Messenger.services.cloudinary.CloudinaryService;
import com.example.Messenger.redisManagers.UserCachingManager;
import com.example.Messenger.util.Convertor;

import java.util.Optional;

public class AutoUploadIcon extends Thread{
    private final CloudinaryService cloudinaryService;
    private final String path;
    private final User owner;
    private final UserCachingManager userCachingManager;
    public AutoUploadIcon(String path, User owner, CloudinaryService cloudinaryService, UserCachingManager userCachingManager){
        this.path = path;
        this.owner = owner;
        this.cloudinaryService = cloudinaryService;
        this.userCachingManager = userCachingManager;
    }

    @Override
    public void run() {
        Optional<String> optionalLink = cloudinaryService.sendIcon(path);
        if(optionalLink.isPresent()){
            userCachingManager.setLinkOfUserIcon(optionalLink.get(), owner);
        }
    }
}
