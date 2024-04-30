package com.example.Messenger.util;

import com.example.Messenger.models.User;
import com.example.Messenger.services.MessageTranslateAPI;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TranslateLoadBalancer {
    private static Map<Integer, MessageTranslateAPI> usersTranslateApiMap = new HashMap<>();

    public void add(int userId){
        usersTranslateApiMap.put(userId, new MessageTranslateAPI());
    }

    static {
        usersTranslateApiMap.put(2, new MessageTranslateAPI());
        usersTranslateApiMap.put(3, new MessageTranslateAPI());
        usersTranslateApiMap.put(4, new MessageTranslateAPI());
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
