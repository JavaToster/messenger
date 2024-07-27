package com.example.Messenger.services.database.message;

import com.example.Messenger.dto.message.TranslateMessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MessageTranslateAPI {
    private final String URL = "https://api.mymemory.translated.net/get?";
    private RestTemplate restTemplate;
    private String fromToLanguages="en|ru";

    public MessageTranslateAPI(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public String translate(String text){
        String finallyUrl = setSettings(text, fromToLanguages);
        
        String translate = sendRequest(finallyUrl).getResponseData().getTranslatedText();
        return translate;
    }

    private String setSettings(String text, String languageMode){
        StringBuffer stringBuffer = new StringBuffer(URL);
        stringBuffer.append(createParameter("q", text));
        stringBuffer.append(createParameter("langpair", languageMode));
        return stringBuffer.toString();
    }

    private String createParameter(String parameterKey, String parameterValue){
        StringBuffer buffer = new StringBuffer(parameterKey);
        buffer.append("=");
        buffer.append(parameterValue);
        buffer.append("&");
        return buffer.toString();
    }

    private TranslateMessageDTO sendRequest(String url){
        return restTemplate.getForObject(url, TranslateMessageDTO.class);
    }

    public void setFromToLanguages(String from, String to) {
        this.fromToLanguages = new StringBuffer(from).append("|").append(to).toString();
    }
}
