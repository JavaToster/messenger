package com.example.Messenger.balancers;

import com.example.Messenger.models.database.user.User;
import com.example.Messenger.services.database.message.MessageTranslateAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class TranslateBalancer {
    private static ConcurrentHashMap<Integer, MessageTranslateAPI> usersTranslateApiMap = new ConcurrentHashMap<>();

    private final RestTemplate restTemplate;

    public void add(int userId){
        usersTranslateApiMap.put(userId, new MessageTranslateAPI(restTemplate));
    }

    static {
        usersTranslateApiMap.put(25, new MessageTranslateAPI(new RestTemplate()));
        usersTranslateApiMap.put(29, new MessageTranslateAPI(new RestTemplate()));
        usersTranslateApiMap.put(36, new MessageTranslateAPI(new RestTemplate()));
    }

    public synchronized void updateTranslateMode(User user, String from, String to){
        MessageTranslateAPI messageTranslateAPI = usersTranslateApiMap.get(user.getId());
        messageTranslateAPI.setFromToLanguages(from, to);
        usersTranslateApiMap.put(user.getId(), messageTranslateAPI);
    }

    public synchronized String translate(User user, String text){
        return usersTranslateApiMap.get(user.getId()).translate(text);
    }
}
