package com.example.Messenger.controllers.rest;

import com.example.Messenger.services.database.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisController {

    private final UserService userService;
}
