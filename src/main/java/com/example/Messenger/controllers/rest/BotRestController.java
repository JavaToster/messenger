package com.example.Messenger.controllers.rest;

import com.example.Messenger.dto.bot.response.BotSuccessfulCreatedDTO;
import com.example.Messenger.dto.bot.response.ErrorResponseDTO;
import com.example.Messenger.dto.bot.response.InformationOfBotDTO;
import com.example.Messenger.dto.bot.response.message.InfoByImageMessageDTO;
import com.example.Messenger.dto.bot.response.message.InfoByTextMessageDTO;
import com.example.Messenger.dto.bot.response.message.InfoOfMessagesDTO;
import com.example.Messenger.dto.bot.response.message.SendMessageForBotDTO;
import com.example.Messenger.models.database.user.Bot;
import com.example.Messenger.services.chat.BotChatService;
import com.example.Messenger.services.user.BotRestService;
import com.example.Messenger.services.user.BotService;
import com.example.Messenger.util.abstractClasses.InfoOfMessage;
import com.example.Messenger.util.exceptions.bot.BotNotFoundException;
import com.example.Messenger.util.exceptions.bot.BotUsernameIsUsedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bot-rest")
public class BotRestController {

    private final BotService botService;
    private final BotChatService botChatService;
    private final BotRestService botRestService;

    @Autowired
    public BotRestController(BotService botService, BotChatService botChatService, BotRestService botRestService) {
        this.botService = botService;
        this.botChatService = botChatService;
        this.botRestService = botRestService;
    }

    @GetMapping("/create")
    public ResponseEntity<BotSuccessfulCreatedDTO> createNewBot(@RequestParam("name") String name){
        return new ResponseEntity<>(new BotSuccessfulCreatedDTO(200, name), HttpStatus.OK);
    }

    @GetMapping("/{botToken}/getMe")
    public ResponseEntity<InformationOfBotDTO> getInformationAboutBot(@PathVariable("botToken") String token){
        Bot bot = botService.findByToken(token);

        return new ResponseEntity<>(new InformationOfBotDTO(200, bot.getUsername(), bot.getToken()), HttpStatus.OK);
    }

    @GetMapping("/{botToken}/getMessages")
    public ResponseEntity<InfoOfMessagesDTO> getMessages(@PathVariable("botToken") String token) {
        InfoOfMessagesDTO info = new InfoOfMessagesDTO(botRestService.getMessages(token));
        for(InfoOfMessage infoOfMessage: info.getMessages()){
            if(infoOfMessage.getClass() == InfoByTextMessageDTO.class){
                System.out.println(((InfoByTextMessageDTO) infoOfMessage).getText());
            }else{
                System.out.println(((InfoByImageMessageDTO) infoOfMessage).getBytesOfImage());
            }
        }
        return new ResponseEntity<>(new InfoOfMessagesDTO(botRestService.getMessages(token)), HttpStatus.OK);
    }

    @GetMapping("/{botToken}/sendMessage")
    public ResponseEntity<InfoByTextMessageDTO> sendMessages(@PathVariable("botToken") String token, @RequestParam("text") String text, @RequestParam("chat_id") int chatId){
        botService.sendMessage(chatId, botService.findByToken(token).getId(), text);

        return new ResponseEntity(new InfoByTextMessageDTO(chatId, text), HttpStatus.OK);
    }

    @PostMapping("/{botToken}/sendMessage")
    public ResponseEntity<InfoByTextMessageDTO> sendMessages(@RequestBody SendMessageForBotDTO requestDTO, @RequestHeader("token") String token){
        botService.sendMessage(requestDTO.getChatId(), botService.getIdByToken(token), requestDTO.getText());

        return new ResponseEntity<>(new InfoByTextMessageDTO(requestDTO.getChatId(), requestDTO.getText()), HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> handlerException(MissingRequestHeaderException e){
        return new ResponseEntity<>(new ErrorResponseDTO(400, "Empty token, please enter token in headers"), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> handleException(BotNotFoundException e){
        return new ResponseEntity<>(new ErrorResponseDTO(404, "Bot not found"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> handleException(MissingServletRequestParameterException e){
        return new ResponseEntity<>(new ErrorResponseDTO(400, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> handleException(BotUsernameIsUsedException e){
        return new ResponseEntity<>(new ErrorResponseDTO(400, e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}