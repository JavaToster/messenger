package com.example.Messenger.controllers;

import com.example.Messenger.models.user.ComplaintOfUser;
import com.example.Messenger.models.user.User;
import com.example.Messenger.services.email.EmailService;
import com.example.Messenger.services.user.ComplaintOfUserService;
import com.example.Messenger.services.user.UserService;
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


    @GetMapping("/blocker/complaint-users")
    public String showAComplaintsOfUser(Model model){
        model.addAttribute("users", userService.getUsersWithComplaint());
        model.addAttribute("user", new User());

        return "/html/forAdmins/blocker/showAComplaintUsers";
    }

    @GetMapping("/blocker/complaint-users/{username}")
    public String showAComplaintOfUserWindow(Model model, @PathVariable("username") String username){
        model.addAttribute("user", userService.findByUsername(username));
        model.addAttribute("complaints", complaintOfUserService.findByUser(username));
        model.addAttribute("complaint", new ComplaintOfUser());

        return "/html/forAdmins/blocker/showAComplaintUserWindow";
    }

    @GetMapping("/blocker/complaint-users/{username}/{id}")
    public String showADescriptionOfComplaint(Model model, @PathVariable("username") String username, @PathVariable("id") int complaintIdByUser){
        model.addAttribute("complaint", complaintOfUserService.findComplaintById(complaintIdByUser));

        return "/html/forAdmins/blocker/showDescriptionOfComplaint";
    }

    @PostMapping("/blocker/complaint-users/{username}/send-warning-message")
    public String sendWarningMessage(@PathVariable("username") String username){
        complaintOfUserService.sendWarningEmail(username);

        return "redirect:/admin/blocker/complaint-users/{username}/";
    }

    @PostMapping("/blocker/complaint-users/{username}/clear-complaints")
    public String clearAUser(@PathVariable("username") String username){
        complaintOfUserService.removeComplaints(username);

        return "redirect:/admin/blocker/complaint-users";
    }

    @PostMapping("/blocker/complaint-users/{username}/ban")
    public String banUser(@PathVariable("username") String username, @RequestParam("time-of-ban") String time){
        userService.banUser(username, time);
        complaintOfUserService.removeComplaints(username);

        return "redirect:/admin/blocker/complaint-users";
    }

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
