package com.example.Messenger.DAO.message;

import com.example.Messenger.models.message.ImageMessage;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.repositories.database.message.PhotoMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ImageMessageDAO {
    private final PhotoMessageRepository photoMessageRepository;
//    public byte[] getBytesOfImage(MessageWrapper messageWrapper) throws IOException {
//        ImageMessage image = photoMessageRepository.findById(messageWrapper.getId()).orElse(null);
//        FileInputStream fileInputStream = new FileInputStream(imagePath+image.getId()+image.getExpansion());
//        return fileInputStream.readAllBytes();
//    }
}
