package com.example.Messenger.controllers.rest;

import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.services.message.PhotoMessageService;
import com.example.Messenger.services.user.UserService;
import com.example.Messenger.services.cache.LanguageOfAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Controller
@RequestMapping("/redis")
public class RedisController {

    private final LanguageOfAppService language;
    private final UserService userService;
    private final PhotoMessageService photoMessageService;

    @Autowired
    public RedisController(LanguageOfAppService language, UserService userService, PhotoMessageService photoMessageService) {
        this.language = language;
        this.userService = userService;
        this.photoMessageService = photoMessageService;
    }

    @GetMapping
    public String upload(){
        return "/html/upload";
    }

    @PostMapping
    public String upload(@RequestParam("image1") MultipartFile file1, @RequestParam("image2") MultipartFile file2) throws IOException{

        return "redirect:/redis";
    }
}
