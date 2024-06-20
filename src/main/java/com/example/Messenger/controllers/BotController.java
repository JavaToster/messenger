package com.example.Messenger.controllers;

import com.example.Messenger.services.chat.BotChatService;
import com.example.Messenger.services.message.MessageService;
import com.example.Messenger.services.message.MessageWrapperService;
import com.example.Messenger.services.user.BotFatherService;
import com.example.Messenger.services.user.BotService;
import com.example.Messenger.util.exceptions.bot.BotUsernameIsUsedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/bot")
public class BotController {

    private final BotFatherService botFatherService;
    private final BotService botService;
    @Value("${bot.father.database.id}")
    private int botFatherDatabaseId;
    private final MessageWrapperService messageWrapperService;
    private final MessageService messageService;
    private final BotChatService botChatService;

    @Autowired
    public BotController(BotFatherService botFatherService, BotService botService, MessageWrapperService messageWrapperService, MessageService messageService, BotChatService botChatService) {
        this.botFatherService = botFatherService;
        this.botService = botService;
        this.messageWrapperService = messageWrapperService;
        this.messageService = messageService;
        this.botChatService = botChatService;
    }

    @PostMapping("/{id}/send-message")
    public String sendMessage(@RequestParam("text") String text, @RequestParam("image") MultipartFile image, @PathVariable("id") int id, @RequestParam("user") int userId){
        if(image.isEmpty()) {
            if (!text.isEmpty()) {
                messageService.sendMessage(id, userId, text);
                if (botChatService.getBotName(id).equals("Bot father")) {
                    String answer;
                    try {
                        answer = botFatherService.createNewBot(text);
                    } catch (BotUsernameIsUsedException e) {
                        answer = e.getMessage();
                    }
                    botService.sendMessage(id, botFatherDatabaseId, answer);
                }
            }
        }else{
            if(text == null || text.isEmpty()){
                text = "";
            }
            messageWrapperService.sendPhoto(image, id, userId, text);
        }
        return "redirect:/messenger/chats/"+id;
    }

}
