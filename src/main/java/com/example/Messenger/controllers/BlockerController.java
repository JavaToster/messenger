package com.example.Messenger.controllers;

import com.example.Messenger.models.user.ComplaintOfUser;
import com.example.Messenger.models.user.User;
import com.example.Messenger.services.database.user.ComplaintOfUserService;
import com.example.Messenger.services.database.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/blocker")
@RequiredArgsConstructor
public class BlockerController {
    private final UserService userService;
    private final ComplaintOfUserService complaintOfUserService;


    @GetMapping("/blocker/complaint-users")
    public String showAComplaintsOfUser(Model model){
        model.addAttribute("users", userService.getUsersWithComplaint());
        model.addAttribute("user", new User());

        return "/html/forAdmins/blocker/showAComplaintUsers";
    }

    @GetMapping("/blocker/complaint-users/{username}")
    public String showAComplaintOfUser(Model model, @PathVariable("username") String username){
        model.addAttribute("user", userService.findByUsername(username));
        model.addAttribute("complaints", complaintOfUserService.findByUser(username));
        model.addAttribute("complaint", new ComplaintOfUser());

        return "/html/forAdmins/blocker/showAComplaintUserWindow";
    }

    @GetMapping("/blocker/complaint-users/{username}/{id}")
    public String showADescriptionOfComplaintByUser(Model model, @PathVariable("id") int complaintIdByUser){
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
}
