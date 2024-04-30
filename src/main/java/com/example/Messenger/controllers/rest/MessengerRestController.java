package com.example.Messenger.controllers.rest;

import com.example.Messenger.dto.channel.CreateChannelDTO;
import com.example.Messenger.dto.group.CreateGroupChatDTO;
import com.example.Messenger.dto.message.TranslateTextRequestDTO;
import com.example.Messenger.dto.util.TranslateModeDTO;
import com.example.Messenger.models.User;
import com.example.Messenger.services.*;
import com.example.Messenger.util.TranslateLoadBalancer;
import com.example.Messenger.util.exceptions.LanguageModeException;
import com.example.Messenger.util.exceptions.LengthOfTextException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/messenger")
public class MessengerRestController {

    private final GroupChatService groupChatService;
    private final UserService userService;
    private final ChannelService channelService;
    private final TranslateLoadBalancer loadBalancer;

    @Autowired
    public MessengerRestController(GroupChatService groupChatService, UserService userService, ChannelService channelService,
                                   TranslateLoadBalancer loadBalancer) {
        this.groupChatService = groupChatService;
        this.userService = userService;
        this.channelService = channelService;
        this.loadBalancer = loadBalancer;
    }

    @PostMapping("/create-group-chat")
    public ResponseEntity<Map<String, Integer>> createGroupChat(@RequestBody CreateGroupChatDTO chatDTO){
        List<User> members = new ArrayList<>();
        chatDTO.getAddedUsers().forEach(user -> members.add(userService.findByUsername(user)));
        Map<String, Integer> responseMap = new HashMap<>();

//        int chatId = groupChatService.createNewGroupChat(members, chatDTO.getGroupName(), userService.findByUsername(chatDTO.getGroupOwner()));

        int chatId = groupChatService.createNewGroupChat(members, chatDTO.getGroupName(), userService.findByUsername(chatDTO.getGroupOwner()));

        responseMap.put("chatId", chatId);

        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }

    @PostMapping("/create-channel")
    public ResponseEntity<Map<String, Integer>> createChannel(@RequestBody CreateChannelDTO channelDTO){
        List<User> subscribers = userService.convertToUser(channelDTO.getChannelSubscribes());

        int chatId = channelService.createNewChannel(subscribers, userService.findByUsername(channelDTO.getChannelOwner()), channelDTO.getChannelName());

        Map<String, Integer> responseMap = new HashMap<>();
        responseMap.put("chatId", chatId);

        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }

    @PostMapping("/set-language")
    public HttpStatus setLanguageMode(@RequestBody TranslateModeDTO translateModeDTO){
        loadBalancer.updateTranslateMode(userService.findById(translateModeDTO.getUserId()), translateModeDTO.getFrom(), translateModeDTO.getTo());
        return HttpStatus.OK;
    }

    @PostMapping("/translate")
    public ResponseEntity<Map<String, String>> translate(@RequestBody TranslateTextRequestDTO requestDTO){
        String translatedText = loadBalancer.translate(userService.findById(requestDTO.getUserId()), requestDTO.getText());

        System.out.println(requestDTO.getText());

        if(requestDTO.getText().length()>500){
            throw new LengthOfTextException("Length of text more 500 characters, please using another translator");
        }
        else if(translatedText.equals(requestDTO.getText())){
            throw new LanguageModeException("Not supported language, please change language in profile");
        }
        return new ResponseEntity<>(Map.of("translateText", translatedText), HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> exceptionHandle(LengthOfTextException e){
        return new ResponseEntity<>(Map.of("errorMessage", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> exceptionHandle(LanguageModeException e){
        return new ResponseEntity<>(Map.of("errorMessage", e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
