package com.example.Messenger.services.database.message;

import com.example.Messenger.DAO.user.UserDAO;
import com.example.Messenger.exceptions.message.ImageReadingException;
import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.models.message.ImageMessage;
import com.example.Messenger.repositories.database.chat.ChatRepository;
import com.example.Messenger.repositories.database.message.PhotoMessageRepository;
import com.example.Messenger.services.cloudinary.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.processing.FilerException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PhotoMessageService {
    private final PhotoMessageRepository photoMessageRepository;
    private final CloudinaryService cloudinaryService;
    private final UserDAO userDAO;
    private final ChatRepository chatRepository;
    @Value("${image.path.messages}")
    private String imagePath;

    @Transactional
    public MessageWrapper sendMessage(MultipartFile file, Chat chat, int userId, String underPhoto) throws IOException, ImageReadingException{
        Optional<String> image = imageIsExist(file);
        if(image.isPresent()){
            return new ImageMessage(chat, userDAO.findById(userId), image.get(), underPhoto, getExpansion(file.getOriginalFilename()));
        }

        String filePath = imagePath+(getLastImageId()+1)+getExpansion(file.getOriginalFilename());
        file.transferTo(new File(filePath));
        String url = cloudinaryService.sendMessage(filePath);
        return new ImageMessage(chat, userDAO.findById(userId), url, underPhoto, getExpansion(file.getOriginalFilename()));
    }

    private int getLastImageId(){
        List<ImageMessage> photos = photoMessageRepository.findAll();
        if(!photos.isEmpty()){
            return photos.getLast().getId();
        }else{
            return 1;
        }
    }


    private Optional<String> imageIsExist(MultipartFile file) throws ImageReadingException {
        try {
            byte[] fileBytes = file.getBytes();
        }catch (IOException e){
            throw new ImageReadingException("Could not processing file");
        }
        List<ImageMessage> images = photoMessageRepository.findAll();
        for(ImageMessage image: images){
            FileInputStream fileInputStream;
            try {
                fileInputStream = new FileInputStream(imagePath + image.getId() + getExpansion(image));
                if (Arrays.equals(fileInputStream.readAllBytes(), file.getBytes())) {
                    return Optional.of(image.getContent());
                }
                fileInputStream.close();
            }catch (FileNotFoundException ignored){
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    private String getExpansion(String fileName){
        return fileName.substring(fileName.indexOf("."), fileName.length());
    }

    public String getExpansion(ImageMessage image){
        return image.getExpansion();
    }

    public byte[] getBytesOfImage(MessageWrapper messageWrapper) throws IOException {
        ImageMessage image = photoMessageRepository.findById(messageWrapper.getId()).orElse(null);
        FileInputStream fileInputStream = new FileInputStream(imagePath+image.getId()+image.getExpansion());
        return fileInputStream.readAllBytes();
    }
}
