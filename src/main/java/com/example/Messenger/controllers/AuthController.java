package com.example.Messenger.controllers;

import com.example.Messenger.models.User;
import com.example.Messenger.services.UserService;
import com.example.Messenger.services.cache.LanguageOfAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final LanguageOfAppService languageOfAppService;

    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder, LanguageOfAppService languageOfAppService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.languageOfAppService = languageOfAppService;
    }

    /** the login page
     * страница аутентификации*/
    @GetMapping("/login")
    public String login(){
        return "/html/auth/login";
    }

    /** the register page
     * страница регистрации*/
    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("user", new User());

        return "/html/auth/register";
    }


    @PostMapping("/register")
    public String registerPost(@ModelAttribute("user") User user){
        //if user is present -> redirect to register page
        //если человек существует -> возвращаем его на страницу регистрации
        if(userService.isUser(user.getUsername())){
            return "redirect:/auth/register";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.register(user);

        return "redirect:/login";
    }

    /**if languages in redis was removed to add
     * если языки в redis были удалены, то добавляем заново*/
    private void addLanguagesToCache(){
        languageOfAppService.addToCache();
    }
}
