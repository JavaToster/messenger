package com.example.Messenger.controllers.rest;

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

    private final EmailService emailService;

    @PostMapping("/send-email")
    public ResponseEntity<HttpStatus> sendEmail(@RequestParam("text") String text){
        emailService.send("miljausha-gizatullina@mail.ru", "First email message", text);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
