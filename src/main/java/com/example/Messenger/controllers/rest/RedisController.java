package com.example.Messenger.controllers.rest;

import com.example.Messenger.models.database.user.User;
import com.example.Messenger.models.redis.user.UserInfoRedis;
import com.example.Messenger.services.database.user.UserService;
import com.example.Messenger.services.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisController {

    private final UserService userService;
}
