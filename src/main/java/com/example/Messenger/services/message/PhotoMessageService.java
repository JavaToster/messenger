package com.example.Messenger.services.message;

import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.models.message.ImageMessage;
import com.example.Messenger.repositories.chat.ChatRepository;
import com.example.Messenger.repositories.message.PhotoMessageRepository;
import com.example.Messenger.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PhotoMessageService {
    private final PhotoMessageRepository photoMessageRepository;
    private final ClodinaryService clodinaryService;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    @Value("${image.path}")
    private String imagePath;

    @Autowired
    public PhotoMessageService(PhotoMessageRepository photoMessageRepository, ClodinaryService clodinaryService, UserRepository userRepository, ChatRepository chatRepository) {
        this.photoMessageRepository = photoMessageRepository;
        this.clodinaryService = clodinaryService;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
    }

    @Transactional
    public void sendMessage(MultipartFile file, int chatId, int userId, String underPhoto) throws IOException {
        Optional<String> image = isImage(file.getBytes());
        if(image.isPresent()){
            ImageMessage imageMessage = new ImageMessage(chatRepository.findById(chatId).orElse(null), userRepository.findById(userId).orElse(null), image.get(), underPhoto, getExpansion(file.getOriginalFilename()));
            photoMessageRepository.save(imageMessage);
            return;
        }

        String filePath = imagePath+(getLastImageId()+1)+getExpansion(file.getOriginalFilename());
        file.transferTo(new File(filePath));
        String url = clodinaryService.sendMessage(filePath);
        ImageMessage imageMessage = new ImageMessage(chatRepository.findById(chatId).orElse(null), userRepository.findById(userId).orElse(null), url, underPhoto, getExpansion(file.getOriginalFilename()));
        photoMessageRepository.save(imageMessage);
    }

    private int getLastImageId(){
        List<ImageMessage> photos = photoMessageRepository.findAll();
        if(!photos.isEmpty()){
            return photos.getLast().getId();
        }else{
            return 1;
        }
    }


    private Optional<String> isImage(byte[] imageBytes) {
        List<ImageMessage> images = photoMessageRepository.findAll();
        for(ImageMessage image: images){
            FileInputStream fileInputStream;
            try {
                fileInputStream = new FileInputStream(imagePath + image.getId() + getExpansion(image));
                if (Arrays.equals(fileInputStream.readAllBytes(), imageBytes)) {
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
        System.out.println(imagePath+image.getId()+image.getExpansion());
        FileInputStream fileInputStream = new FileInputStream(imagePath+image.getId()+image.getExpansion());
        return fileInputStream.readAllBytes();
    }
}
