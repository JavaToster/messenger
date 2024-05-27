package com.example.Messenger.services.message;

import com.example.Messenger.dto.message.TranslateMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MessageTranslateAPI {
    private String URL = "https://api.mymemory.translated.net/get";
    private RestTemplate restTemplate = new RestTemplate();
    private boolean parameterIs;
    private String fromToLanguages="en|ru";
    public MessageTranslateAPI (){

    }
    @Autowired
    public MessageTranslateAPI(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public synchronized String translate(String text){
        addParameter("q", text);
        addParameter("langpair", fromToLanguages);

        String translate = sendRequest().getResponseData().getTranslatedText();
        clearParameters();
        return translate;
    }

    private synchronized void clearParameters() {
        URL = URL.substring(0, URL.indexOf("?"));
        parameterIs = false;
    }

    private synchronized void addParameter(String parameterName, String parameterValue){
        StringBuffer stringBuffer = new StringBuffer(URL);
        if(!parameterIs){
            stringBuffer.append("?"+parameterName+"="+parameterValue+"&");
            parameterIs = !parameterIs;
            URL=stringBuffer.toString();
            return;
        }
        stringBuffer.append(parameterName+"="+parameterValue);
        URL = stringBuffer.toString();
    }

    private synchronized TranslateMessageDTO sendRequest(){
        return restTemplate.getForObject(URL, TranslateMessageDTO.class);
    }

    public synchronized void setFromToLanguages(String from, String to) {
        this.fromToLanguages = from+"|"+to;
    }
}
