package com.example.Messenger.util.browser;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class Browser {

    private final RestTemplate restTemplate;
    private final Parser parser;

    public String getHtmlFromUrl(String url){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.TEXT_HTML));

        HttpEntity<String> entity = new HttpEntity<>("body", httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        String html = response.getBody();
        String updatedHtml = parser.addAttributeToHtml(html, "supernova_search_form", "action", "/browser//");
        return updatedHtml;
    }
}
