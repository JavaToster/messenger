package com.example.Messenger.models.redis;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;


@Data
@RedisHash("languages")
public class LanguageOfApp {

    @Id
    private Integer id;

    @Indexed
    private String type;

    //for show main window

    private String messengerHeader;
    private String profile;
    private String logout;
    private String findChat;
    private String findChatButton;
    private String findChatDescription;
    private String createPrivateChat;
    private String createGroup;
    private String createChannel;
    private String createChatButton;

    //for show chat

    private String translateMessageButton;
    private String inputMessagePlaceholder;
    private String sendMessageButton;
    private String subscribers;
    private String groupMembers;

    //for show profile

    private String changeLanguage;
    private String toSendOfUser;

    //for create private chat window

    private String selectInterlocutorLabel;

    //for create group window and channel chat

    private String enterGroupName;
    private String enterChannelName;

}
