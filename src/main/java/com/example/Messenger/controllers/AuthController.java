package com.example.Messenger.controllers;

import com.example.Messenger.models.user.User;
import com.example.Messenger.services.user.UserService;
import com.example.Messenger.services.cache.LanguageOfAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String login(@RequestParam(value = "error", required = false) String error, Model model){
        model.addAttribute("error", !(error==null));

        return "/html/auth/login";
    }

    /** the register page
     * страница регистрации*/
    @GetMapping("/register")
    public String register(Model model, @RequestParam(value = "error", required = false) String error){
        model.addAttribute("error", !(error==null));
        model.addAttribute("user", new User());

        return "/html/auth/register";
    }


    @PostMapping("/register")
    public String registerPost(@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName, @RequestParam("username") String username,
                               @RequestParam("password") String password, @RequestParam("phone") String phone, @RequestParam("language") String lang){

        //if user is present -> redirect to register page
        //если человек существует -> возвращаем его на страницу регистрации
//        if(userService.isUser(user.getUsername())){
//            return "redirect:/auth/register";
//        }

        if(userService.isUser(username)){
            return "redirect:/auth/register?error";
        }

        userService.register(new User(firstName, lastName, username, passwordEncoder.encode(password), phone, lang));
        return "redirect:/auth/login";
    }

    /**if languages in redis was removed to add
     * если языки в redis были удалены, то добавляем заново*/
    private void addLanguagesToCache(){
        languageOfAppService.addToCache();
    }
}
