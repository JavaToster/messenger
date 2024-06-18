package com.example.Messenger.controllers;

import com.example.Messenger.dto.user.RegisterUserDTO;
import com.example.Messenger.models.user.User;
import com.example.Messenger.services.user.UserService;
import com.example.Messenger.services.languageOfApp.LanguageOfAppService;
import com.example.Messenger.util.balancer.UserStatusBalancer;
import com.example.Messenger.util.enums.StatusOfEqualsCodes;
import com.example.Messenger.util.enums.UserStatus;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final LanguageOfAppService languageOfAppService;
    private final UserStatusBalancer statusBalancer;

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
        model.addAttribute("register_user", new RegisterUserDTO());

        return "/html/auth/register";
    }


    @PostMapping("/register")
    public String registerPost(@Valid @ModelAttribute("register_user") RegisterUserDTO registerUser){
        if(userService.isUser(registerUser.getUsername(), registerUser.getEmail())){
            return "redirect:/auth/register?error";
        }

        registerUser.setPassword(passwordEncoder.encode(registerUser.getPassword()));
        userService.register(new User(registerUser), registerUser.getIcon());
        return "redirect:/auth/login";
    }

    @GetMapping("/forgot_password")
    public String forgotPassword(@RequestParam(value = "step", required = false) Integer step, @RequestParam(value = "error", required = false) String error,
                                 Model model, @CookieValue(value = "restoreEmail", required = false) String email){
        // параметр error нужен для вывода текста, то есть неправильный email(для 1 этапа) и неправильный код(для 2 этапа)
        if(error != null){
            model.addAttribute("error", true);
        }

        if(step == null || step == 1){
            return "/html/auth/forgot_password_1";
        }else if(step == 2){
            if(email == null || email.isEmpty()){
                return "redirect:/auth/forgot_password?step=1";
            }
            return "/html/auth/forgot_password_2";
        }else{
            return "redirect:/auth/forgot_password";
        }
    }

    @GetMapping("/forgot_password_2")
    public String forgotPassword2(@CookieValue(value = "restoreEmail", required = false) String email){
        if(email == null || email.isEmpty()){
            return "redirect:/auth/forgot_password";
        }
        return "/html/auth/forgot_password_2";
    }

    @PostMapping("/forgot_password_1")
    public String sendCodeToEmailToRestore(@RequestParam(value = "email", required = false) String email, HttpServletResponse response){
        if(email == null || email.isEmpty()){
            return "redirect:/auth/forgot_password?error";
        }

        if(!userService.isEmail(email)){
            return "redirect:/auth/forgot_password?error";
        }
        userService.sendCodeToRestore(response, email);
        return "redirect:/auth/forgot_password?step=2";
    }

    @PostMapping("/check_restore_code")
    public String checkRestoreCode(@RequestParam("restoreCode") int code, @CookieValue(value = "restoreEmail", required = false) String email){
        if(email == null){
            return "redirect:/auth/forgot_password";
        }
        StatusOfEqualsCodes status = userService.checkRestoreCode(email, code);
        if(status == StatusOfEqualsCodes.EQUAL){
            statusBalancer.addUserByEmail(email, UserStatus.CHANGE_PASSWORD);
            return "redirect:/auth/change_password";
        }else if(status == StatusOfEqualsCodes.NOT_EQUAL){
            return "redirect:/auth/forgot_password?step=2&error";
        }else{
            return "redirect:/auth/login";
        }
    }

    @PostMapping("/change_password")
    public String changePassword(@RequestParam("newPassword") String password, @CookieValue("restoreEmail") String email,
                                 HttpServletResponse response){
        if(password == null || password.isEmpty()){
            return "redirect:/auth/change_password?error";
        }

        userService.changePasswordByEmail(email, password);
        statusBalancer.removeUserFromEmail(email);
        UserService.deleteCookie(response, "restoreEmail");
        return "redirect:/auth/login";
    }

    @GetMapping("/change_password")
    public String changePasswordGet(@CookieValue(value = "restoreEmail", required = false) String email, @RequestParam(value = "error", required = false) String error,
                                    Model model){
        if(error != null){
            model.addAttribute("error", true);
        }

        if(!statusBalancer.checkStatusByEmail(email, UserStatus.CHANGE_PASSWORD)){
            return "redirect:/auth/login";
        }

        return "/html/auth/change_password";
    }

    /**if languages in redis was removed to add
     * если языки в redis были удалены, то добавляем заново*/
    @Deprecated
    private void addLanguagesToCache(){
        languageOfAppService.addToCache();
    }
}
