package com.example.Messenger.services.user;

import com.example.Messenger.models.user.IconOfUser;
import com.example.Messenger.models.user.User;
import com.example.Messenger.repositories.user.IconOfUserRepository;
import com.example.Messenger.util.CloudinaryService;
import com.example.Messenger.util.threads.AutoUploadIcon;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class IconOfUserService {
    private final IconOfUserRepository iconOfUserRepository;
    private final CloudinaryService cloudinaryService;
    @Value("${image.path.user.icons}")
    private String imagePath;

    @Transactional
    public void createNewIcon(MultipartFile icon, User owner) throws IOException {
        String filePath = imagePath+(getLastImageId()+1)+getExpansion(icon.getOriginalFilename());
        icon.transferTo(new File(filePath));
        AutoUploadIcon uploadIcon = new AutoUploadIcon(filePath, owner, cloudinaryService, iconOfUserRepository);
        uploadIcon.start();
    }

    private int getLastImageId(){
        List<IconOfUser> photos = iconOfUserRepository.findAll();
        if(!photos.isEmpty()){
            return photos.getLast().getId();
        }else{
            return 0;
        }
    }

    private String getExpansion(String fileName){
        return fileName.substring(fileName.indexOf("."), fileName.length());
    }
}
