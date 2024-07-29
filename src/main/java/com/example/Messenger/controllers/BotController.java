package com.example.Messenger.controllers;

import com.example.Messenger.DAO.chat.BotChatDAO;
import com.example.Messenger.DAO.chat.ChatDAO;
import com.example.Messenger.services.database.chat.BotChatService;
import com.example.Messenger.services.database.message.MessageWrapperService;
import com.example.Messenger.util.Convertor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/messenger/bot")
@RequiredArgsConstructor
public class BotController {

    private final BotChatService botChatService;

    @PostMapping("/{id}/send-message")
    public String sendMessageToBot(@PathVariable("id") int chatId, @RequestParam("user") int userId, @RequestParam("text") String text) {
        botChatService.sendMessage(chatId, userId, text);

        return "redirect:/messenger/chats/"+chatId;
    }


}
