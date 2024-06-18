package com.example.Messenger.util.balancer;

import com.example.Messenger.models.user.User;
import com.example.Messenger.services.message.MessageTranslateAPI;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TranslateBalancer {
    private static ConcurrentHashMap<Integer, MessageTranslateAPI> usersTranslateApiMap = new ConcurrentHashMap<>();

    public void add(int userId){
        usersTranslateApiMap.put(userId, new MessageTranslateAPI());
    }

    static {
        usersTranslateApiMap.put(25, new MessageTranslateAPI());
        usersTranslateApiMap.put(29, new MessageTranslateAPI());
        usersTranslateApiMap.put(36, new MessageTranslateAPI());
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
