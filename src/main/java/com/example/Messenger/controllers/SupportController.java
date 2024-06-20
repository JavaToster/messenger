package com.example.Messenger.controllers;

import com.example.Messenger.services.message.MessageWrapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/support")
@RequiredArgsConstructor
public class SupportController {
    private final MessageWrapperService messageWrapperService;

    @GetMapping("")
    public String mainWindow(Model model){
//        model.addAttribute("messages", messageWrapperService.findSupportChats());

        return "/html/forAdmins/support/mainWindow";
    }
}
