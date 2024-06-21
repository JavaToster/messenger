package com.example.Messenger.controllers;

import com.example.Messenger.models.database.user.User;
import com.example.Messenger.services.database.user.ComplaintOfUserService;
import com.example.Messenger.services.database.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final ComplaintOfUserService complaintOfUserService;

    @GetMapping("/users")
    public String usersList(Model model){
        model.addAttribute("users", userService.findAll());
        model.addAttribute("user", new User());

        return "/html/forAdmins/admin/setAdminsList";
    }

    @PostMapping("/blocker/{username}/set")
    public String setBlocker(@PathVariable("username") String username){
        userService.setBlocker(username);

        return "redirect:/admin/users";
    }

    @PostMapping("/blocker/{username}/unset")
    public String unsetBlocker(@PathVariable("username") String username){
        userService.unsetBlocker(username);

        return "redirect:/admin/users";
    }
}
