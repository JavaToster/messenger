package com.example.Messenger.controllers;

import com.example.Messenger.dto.ChatDTO;
import com.example.Messenger.dto.user.InfoOfUserDTO;
import com.example.Messenger.services.database.SettingsOfUserService;
import com.example.Messenger.services.database.chat.ChatService;
import com.example.Messenger.services.database.user.ComplaintOfUserService;
import com.example.Messenger.services.database.user.MessengerUserService;
import com.example.Messenger.services.database.user.UserService;
import com.example.Messenger.services.email.redis.languageOfApp.LanguageOfAppService;
import com.example.Messenger.util.Convertor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ChatService chatService;
    private final MessengerUserService messengerUserService;
    private final LanguageOfAppService languageOfAppService;
    private final Convertor convertor;
    private final ComplaintOfUserService complaintOfUserService;
    private final SettingsOfUserService settingsOfUserService;

    @GetMapping("/profile")
    public String profile(Model model, @CookieValue("username") String username){
        List<ChatDTO> chats = convertor.convertToChatDTO(userService.FIND_CHATS_BY_USERNAME(userService.findByUsername(username)), username);

        model.addAttribute("chat", new ChatDTO());
        model.addAttribute("chats", chats);
        model.addAttribute("language", languageOfAppService.getLanguage(userService.findByUsername(username).getSettingsOfUser().getLang()));
        model.addAttribute("infoOfUser", userService.findUserInfoByUsername(username, username));

        return "/html/user/profile";
    }
    @GetMapping("/{username}")
    public String showUserProfile(@PathVariable("username") String username, Model model, @CookieValue("username") String myUsername){
        // нужно чтобы проверять не смотрит ли пользователь на свое же окно
        if (username.equals(myUsername)) {
            return "redirect:/user/profile";
        }

        InfoOfUserDTO infoOfUserDTO = userService.findUserInfoByUsername(username, myUsername);
        model.addAttribute("infoOfUser", infoOfUserDTO);
        model.addAttribute("myUsername", myUsername);
        model.addAttribute("url", new String());

        return "/html/user/showUserProfile";
    }

    @PostMapping("/{username}/send-message")
    public String sendMessageToUser(@PathVariable("username") String usernameOfBotOrUser, @RequestParam("from") String fromUsername){
        int id = chatService.createPrivateOrBotChat(usernameOfBotOrUser, fromUsername);

        return "redirect:/messenger/chats/"+id;
    }

    @PostMapping("/{username}/complaint")
    public String sendAComplain(@RequestParam("complaint-text") String complaintText, @PathVariable("username") String username, @CookieValue("username") String fromUsername){
        complaintOfUserService.addComplaint(username, complaintText, fromUsername);

        return "redirect:/user/"+username;
    }

    @PostMapping("/{id}/changeUserLang")
    public String changeAppLang(@RequestParam("app-lang") String lang, @PathVariable("id") int id){
        settingsOfUserService.changeAppLanguage(id, lang);

        return "redirect:/user/profile";
    }
}
