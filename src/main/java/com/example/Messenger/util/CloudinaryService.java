package com.example.Messenger.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String sendMessage(String path) throws IOException {
        File file = new File(path);
        return (String) cloudinary.uploader().upload(file, ObjectUtils.asMap("resource_type", "image")).get("secure_url");
    }
    public String sendIcon(String path) throws IOException {
        File file = new File(path);
        String link =  (String) cloudinary.uploader().upload(file, ObjectUtils.asMap("resource_type", "image")).get("secure_url");
        file.delete();
        return link;
    }
}
