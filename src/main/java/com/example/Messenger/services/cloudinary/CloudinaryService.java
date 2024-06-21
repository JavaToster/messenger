package com.example.Messenger.services.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public Optional<String> sendMessage(String path) {
        try {
            File file = new File(path);
            return Optional.of( (String) cloudinary.uploader().upload(file, ObjectUtils.asMap("resource_type", "image")).get("secure_url"));
        }catch (IOException e){
            return Optional.empty();
        }
    }
    public Optional<String> sendIcon(String path){
        try {
            File file = new File(path);
            Optional<String> url =  Optional.of((String) cloudinary.uploader().upload(file, ObjectUtils.asMap("resource_type", "image")).get("secure_url"));
            System.out.println(url);
            file.delete();
            return url;
        }catch (IOException e){
            return Optional.empty();
        }
    }
}
