package com.example.Messenger.services.translate;

import com.example.Messenger.dto.message.TranslateMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TranslateService {
    private final String URL = "https://api.mymemory.translated.net/get?";
    private RestTemplate restTemplate;

    @Autowired
    public TranslateService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public String translate(String text, String translateMode){
        String finallyUrl = setSettings(text, translateMode);

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
}
