package com.example.Messenger.util.threads;

import com.example.Messenger.models.database.user.IconOfUser;
import com.example.Messenger.models.database.user.User;
import com.example.Messenger.repositories.database.user.IconOfUserRepository;
import com.example.Messenger.repositories.database.user.UserRepository;
import com.example.Messenger.services.cloudinary.CloudinaryService;
import com.example.Messenger.services.database.user.IconOfUserService;
import com.example.Messenger.services.database.user.UserService;
import com.example.Messenger.services.redis.user.UserCachingService;

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
