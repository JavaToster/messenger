package com.example.Messenger.controllers.rest;

import com.example.Messenger.dto.message.rest.SendMessageRequestDTO;
import com.example.Messenger.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest-messenger")
public class MessengerControllerRest {

    private final MessageService messageService;

    @Autowired
    public MessengerControllerRest(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send-message")
    public ResponseEntity<HttpStatus> sendMessage(@RequestBody SendMessageRequestDTO requestDTO){
        messageService.sendMessage(requestDTO.getChat_id(), requestDTO.getUser_id(), requestDTO.getText());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
