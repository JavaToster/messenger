package com.example.Messenger.controllers.rest;

import com.example.Messenger.services.database.SettingsOfUserService;
import com.example.Messenger.services.database.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/rest")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;
    private final SettingsOfUserService settingsOfUserService;


}
