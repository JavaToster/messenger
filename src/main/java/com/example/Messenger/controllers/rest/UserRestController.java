package com.example.Messenger.controllers.rest;

import com.example.Messenger.dto.cache.ChangeLangRequestDTO;
import com.example.Messenger.services.user.UserService;
import com.example.Messenger.util.exceptions.LanguageNotSupportedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/rest")
public class UserRestController {
    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/change-app-lang")
    public ResponseEntity<HttpStatus> changeAppLang(@RequestBody ChangeLangRequestDTO requestDTO){
        System.out.println(requestDTO.getLang());
        userService.changeLang(requestDTO.getUserId(), requestDTO.getLang());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<HttpStatus> handleException(LanguageNotSupportedException e){
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
