package com.example.Messenger.controllers.rest;

import com.example.Messenger.dto.channel.CreateChannelDTO;
import com.example.Messenger.dto.group.CreateGroupChatDTO;
import com.example.Messenger.dto.message.TranslateTextRequestDTO;
import com.example.Messenger.dto.message.rest.ForwardMessageRequestDTO;
import com.example.Messenger.dto.message.rest.ForwardMessageResponseDTO;
import com.example.Messenger.dto.util.TranslateModeDTO;
import com.example.Messenger.models.message.ForwardMessage;
import com.example.Messenger.models.user.User;
import com.example.Messenger.services.chat.ChannelService;
import com.example.Messenger.services.chat.GroupChatService;
import com.example.Messenger.services.message.ForwardMessageService;
import com.example.Messenger.services.user.UserService;
import com.example.Messenger.util.Convertor;
import com.example.Messenger.util.balancer.TranslateBalancer;
import com.example.Messenger.util.exceptions.ErrorResponse;
import com.example.Messenger.util.exceptions.LanguageModeException;
import com.example.Messenger.util.exceptions.LengthOfTextException;
import com.example.Messenger.util.exceptions.UserNotOwnerOfChannelException;
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
    private final TranslateBalancer loadBalancer;
    private final Convertor convertor;
    private final ForwardMessageService forwardMessageService;

    @Autowired
    public MessengerRestController(GroupChatService groupChatService, UserService userService, ChannelService channelService,
                                   TranslateBalancer loadBalancer, Convertor convertor, ForwardMessageService forwardMessageService) {
        this.groupChatService = groupChatService;
        this.userService = userService;
        this.channelService = channelService;
        this.loadBalancer = loadBalancer;
        this.convertor = convertor;
        this.forwardMessageService = forwardMessageService;
    }

    @PostMapping("/create-group-chat")
    public ResponseEntity<Map<String, Integer>> createGroupChat(@RequestBody CreateGroupChatDTO chatDTO){
        List<User> members = new ArrayList<>();
        chatDTO.getAddedUsers().forEach(user -> members.add(userService.findByUsername(user)));
        Map<String, Integer> responseMap = new HashMap<>();

        int chatId = groupChatService.createNewGroupChat(members, chatDTO.getGroupName(), userService.findByUsername(chatDTO.getGroupOwner()));

        responseMap.put("chatId", chatId);

        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }

    @PostMapping("/create-channel")
    public ResponseEntity<Map<String, Integer>> createChannel(@RequestBody CreateChannelDTO channelDTO){
        List<User> subscribers = convertor.convertToUser(channelDTO.getChannelSubscribes());

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

        if(requestDTO.getText().length()>500){
            throw new LengthOfTextException("Length of text more 500 characters, please using another translator");
        }
        else if(translatedText.equals(requestDTO.getText())){
            throw new LanguageModeException("Not supported language, please change language in profile");
        }
        return new ResponseEntity<>(Map.of("translateText", translatedText), HttpStatus.OK);
    }

    @PostMapping("/forward")
    public ResponseEntity<ForwardMessageResponseDTO> forward(@RequestBody ForwardMessageRequestDTO request){
        int toChatId = request.getToChatId();
        int willForwardMessageId = request.getForwardMessageId();
        int ownerId = request.getOwnerId();
        int fromChatId = request.getFromChatId();

        if (channelService.isChannel(toChatId)) {
            if(!forwardMessageService.isUserIsOwnerOfChannel(toChatId, ownerId)){
                throw new UserNotOwnerOfChannelException();
            }
        }

        ForwardMessage forwardMessage = forwardMessageService.forward(willForwardMessageId, toChatId, ownerId, fromChatId);

        return new ResponseEntity<>(convertor.convertToForwardMessageResponseDTO(forwardMessage, userService.findById(ownerId).getUsername()), HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> exceptionHandle(LengthOfTextException e){
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> exceptionHandle(LanguageModeException e){
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> exceptionHandle(UserNotOwnerOfChannelException e){
        return new ResponseEntity<>(new ErrorResponse("You isn't owner of this channel"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> exceptionHandle(NullPointerException e){
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
