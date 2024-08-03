package com.example.Messenger.controllers.rest;

import com.example.Messenger.util.browser.Browser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/browser")
@RequiredArgsConstructor
public class BrowserController {

    private final Browser browser;

    @GetMapping("/find")
    public String getPage(@RequestParam(value = "url", required = false, defaultValue = "http://localhost:8080/messenger") String url){
        return browser.getHtmlFromUrl(url);
    }
}
