package com.example.Messenger.controllers.rest;

import com.example.Messenger.services.UserService;
import com.example.Messenger.services.cache.LanguageOfAppService;
import com.example.Messenger.util.enums.LanguageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/redis")
public class RedisController {

    private final LanguageOfAppService language;
    private final UserService userService;

    @Autowired
    public RedisController(LanguageOfAppService language, UserService userService) {
        this.language = language;
        this.userService = userService;
    }

    @GetMapping
    public String uploadFile(){
        return "/html/upload";
    }

    @PostMapping
    public String getImage(@RequestParam("image") MultipartFile image) throws IOException {
        image.transferTo(new File("C:/Java/Messenger/src/main/resources/static/images/3.jpg"));
        return "redirect:/redis";
    }

    @GetMapping("/show")
    public String showImage(){
        return "/html/showImage";
    }
}
