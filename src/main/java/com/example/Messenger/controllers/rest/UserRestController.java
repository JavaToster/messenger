package com.example.Messenger.controllers.rest;

import com.example.Messenger.dto.cache.ChangeLangRequestDTO;
import com.example.Messenger.services.database.SettingsOfUserService;
import com.example.Messenger.services.database.user.UserService;
import com.example.Messenger.util.exceptions.LanguageNotSupportedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/rest")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;
    private final SettingsOfUserService settingsOfUserService;


}
