package com.example.Messenger.controllers;

import com.example.Messenger.DAO.chat.ChatDAO;
import com.example.Messenger.models.chat.BotFatherChat;
import com.example.Messenger.services.database.chat.BotChatService;
import com.example.Messenger.services.database.message.MessageService;
import com.example.Messenger.services.database.message.MessageWrapperService;
import com.example.Messenger.services.database.user.BotFatherService;
import com.example.Messenger.services.database.user.BotService;
import com.example.Messenger.util.Convertor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/messenger/bot-father")
@RequiredArgsConstructor
public class BotFatherController {

    private final Convertor convertor;
    private final ChatDAO chatDAO;

//    @GetMapping("/chats/{id}")
//    public String createWindow(@PathVariable("id") int chatId, @CookieValue("username") String username, Model model){
//
//    }

}
