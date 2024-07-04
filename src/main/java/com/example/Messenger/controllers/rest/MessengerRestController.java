package com.example.Messenger.controllers.rest;

import com.example.Messenger.dto.chat.channel.CreateChannelDTO;
import com.example.Messenger.dto.chat.group.CreateGroupChatDTO;
import com.example.Messenger.dto.message.TranslateTextRequestDTO;
import com.example.Messenger.dto.message.rest.ForwardMessageRequestDTO;
import com.example.Messenger.dto.message.rest.ForwardMessageResponseDTO;
import com.example.Messenger.dto.util.TranslateModeDTO;
import com.example.Messenger.models.message.ForwardMessage;
import com.example.Messenger.models.user.User;
import com.example.Messenger.services.database.SettingsOfUserService;
import com.example.Messenger.services.database.chat.ChannelService;
import com.example.Messenger.services.database.chat.GroupChatService;
import com.example.Messenger.services.database.message.ForwardMessageService;
import com.example.Messenger.services.database.user.UserService;
import com.example.Messenger.services.translate.TranslateService;
import com.example.Messenger.util.Convertor;
import com.example.Messenger.balancers.TranslateBalancer;
import com.example.Messenger.util.exceptions.ErrorResponse;
import com.example.Messenger.util.exceptions.LanguageModeException;
import com.example.Messenger.util.exceptions.LengthOfTextException;
import com.example.Messenger.util.exceptions.UserNotOwnerOfChannelException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/messenger")
@RequiredArgsConstructor
public class MessengerRestController {

    private final GroupChatService groupChatService;
    private final UserService userService;
    private final ChannelService channelService;
    private final TranslateBalancer loadBalancer;
    private final Convertor convertor;
    private final ForwardMessageService forwardMessageService;
    private final SettingsOfUserService settingsOfUserService;
    private final TranslateService translateService;

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
        List<User> subscribers = convertor.convertToUserByUsername(channelDTO.getChannelSubscribes());

        int chatId = channelService.createNewChannel(subscribers, userService.findByUsername(channelDTO.getChannelOwner()), channelDTO.getChannelName());

        Map<String, Integer> responseMap = new HashMap<>();
        responseMap.put("chatId", chatId);

        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }

    @PostMapping("/set-translate-message-language")
    public HttpStatus setTranslateMessageLanguageMode(@RequestBody TranslateModeDTO translateModeDTO){
        settingsOfUserService.changeUserTranslateMessageLangMode(translateModeDTO);

        return HttpStatus.OK;
    }

    @PostMapping("/translate")
    public ResponseEntity<Map<String, String>> translate(@RequestBody TranslateTextRequestDTO requestDTO){
        String translatedText = translateService.translate(requestDTO.getText(), userService.getSettings(requestDTO.getUserId()).getTranslateMessageMode());

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
