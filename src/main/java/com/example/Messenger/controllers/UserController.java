package com.example.Messenger.controllers;

import com.example.Messenger.dto.ChatDTO;
import com.example.Messenger.services.chat.ChatService;
import com.example.Messenger.services.user.MessengerUserService;
import com.example.Messenger.services.user.UserService;
import com.example.Messenger.services.cache.LanguageOfAppService;
import com.example.Messenger.util.Convertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final ChatService chatService;
    private final MessengerUserService messengerUserService;
    private final LanguageOfAppService languageOfAppService;
    private final Convertor convertor;

    @Autowired
    public UserController(UserService userService, ChatService chatService, MessengerUserService messengerUserService, LanguageOfAppService languageOfAppService, Convertor convertor) {
        this.userService = userService;
        this.chatService = chatService;
        this.messengerUserService = messengerUserService;
        this.languageOfAppService = languageOfAppService;
        this.convertor = convertor;
    }

    @GetMapping("/profile")
    public String profile(Model model, @CookieValue("username") String username){
        List<ChatDTO> chats = convertor.convertToChatDTO(userService.findChatsByUsername(username), username);

        model.addAttribute("userId", userService.findByUsername(username).getId());
        model.addAttribute("chat", new ChatDTO());
        model.addAttribute("chats", chats);
        model.addAttribute("username", username);
        model.addAttribute("language", languageOfAppService.getLanguage(userService.findByUsername(username).getLang()));

        return "/html/user/profile";
    }
    @GetMapping("/{username}")
    public String showUserProfile(@PathVariable("username") String username, Model model, @CookieValue("username") String myUsername){
        model.addAttribute("user", userService.findByUsername(username));
        model.addAttribute("myUsername", myUsername);
        model.addAttribute("language", languageOfAppService.getLanguage(userService.findByUsername(username).getLang()));

        return "/html/user/showUserProfile";
    }

    @PostMapping("/{username}/send-message")
    public String sendMessageToUser(@PathVariable("username") String username, @RequestParam("from") String fromUsername){
        int id = chatService.createPrivateOrBotChat(messengerUserService.findByUsername(username), fromUsername);

        return "redirect:/messenger/chats/"+id;
    }

    @PostMapping("/{id}/changeUserLang")
    public String changeAppLang(@RequestParam("app-lang") String lang, @PathVariable("id") int id){
        userService.changeLang(id, lang);
        return "redirect:/user/profile";
    }
}
