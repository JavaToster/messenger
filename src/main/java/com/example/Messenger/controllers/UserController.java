package com.example.Messenger.controllers;

import com.example.Messenger.dto.ExceptionMessageDTO;
import com.example.Messenger.dto.user.ComplaintDTO;
import com.example.Messenger.dto.user.UserProfileDTO;
import com.example.Messenger.exceptions.user.UserNotFoundException;
import com.example.Messenger.services.database.SettingsOfUserService;
import com.example.Messenger.services.database.chat.ChatService;
import com.example.Messenger.services.database.user.ComplaintOfUserService;
import com.example.Messenger.services.database.user.UserService;
import com.example.Messenger.util.Convertor;
import com.example.Messenger.validators.user.UserValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ChatService chatService;
    private final Convertor convertor;
    private final SettingsOfUserService settingsOfUserService;
    private final ComplaintOfUserService complaintOfUserService;
    private final UserValidator userValidator;
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> profile(Principal principal){
        UserProfileDTO profile = convertor.convertToUserProfileDTO(userService.findByUsername(principal.getName()), userService.findChatOfUser(principal.getName()));
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }
    @GetMapping("/{username}")
    public ResponseEntity<UserProfileDTO> showUserProfile(@PathVariable("username") String username, Principal principal){
        if (username.equals(principal.getName())){
            return profile(principal);
        }

        return new ResponseEntity<>(convertor.convertToUserProfileDTO(userService.findByUsername(username), userService.findCommonChats(username, principal.getName())), HttpStatus.OK);
    }

    @PostMapping("/{username}/complaint")
    public ResponseEntity<HttpStatus> sendAComplaint(@RequestBody @Valid ComplaintDTO complaintDTO, BindingResult errors, @PathVariable("username") String username, Principal principal){
        userValidator.validate(errors);
        complaintOfUserService.addComplaint(username, complaintDTO.getComplaint(), principal.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionMessageDTO> handleException(UserNotFoundException e){
        return new ResponseEntity<>(new ExceptionMessageDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
