package com.example.Messenger.services.message;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
public class ClodinaryService {

    private final Cloudinary cloudinary;

    @Autowired
    public ClodinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String sendMessage(String path) throws IOException {
        File file = new File(path);
        return (String) cloudinary.uploader().upload(file, ObjectUtils.asMap("resource_type", "image")).get("secure_url");
    }
}
