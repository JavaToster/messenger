package com.example.Messenger.services.redis.languageOfApp;

import com.example.Messenger.redisModel.languageData.LanguageOfApp;
import com.example.Messenger.repositories.redis.languageOfApp.LanguageOfAppRepository;
import com.example.Messenger.util.enums.LanguageType;
import com.example.Messenger.util.exceptions.LanguageNotSupportedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LanguageOfAppService {
    private final LanguageOfAppRepository languageOfAppRepository;

    // for admins, using when admin add new language for application
    public void addLanguage(LanguageOfApp language){
        languageOfAppRepository.save(language);
    }

    public LanguageOfApp getLanguage(LanguageType languageType){
        String codeOfLanguage;
        switch (languageType){
            case ENGLISH -> codeOfLanguage = "en";
            case RUSSIAN -> codeOfLanguage = "ru";
            case CHINESE -> codeOfLanguage = "zh";
            case GERMAN -> codeOfLanguage = "de";
            case ITALIAN -> codeOfLanguage = "it";
            default -> throw new LanguageNotSupportedException();
        }
        return languageOfAppRepository.findByType(codeOfLanguage).orElse(null);
    }

    public void addToCache() {
        LanguageOfApp english = new LanguageOfApp();
        english.setType("en");
        english.setMessengerHeader("Messenger");
        english.setProfile("profile");
        english.setLogout("logout");
        english.setFindChat("Find chat");
        english.setFindChatButton("Find");
        english.setFindChatDescription("enter any word to find the chat");
        english.setCreatePrivateChat("Private");
        english.setCreateGroup("Group");
        english.setCreateChannel("Channel");
        english.setCreateChatButton("Create");
        english.setTranslateMessageButton("Translate");
        english.setInputMessagePlaceholder("Enter your message here...");
        english.setSendMessageButton("Send!");
        english.setSubscribers("Subscribers");
        english.setGroupMembers("Members");
        english.setChangeLanguage("Change");
        english.setToSendOfUser("Send");
        english.setSelectInterlocutorLabel("Select interlocutor");
        english.setEnterGroupName("Enter group name");
        english.setEnterChannelName("Enter channel name");

        LanguageOfApp russian = new LanguageOfApp();
        russian.setType("ru");
        russian.setMessengerHeader("Мессенджер");
        russian.setProfile("профиль");
        russian.setLogout("выйти");
        russian.setFindChat("Искать чат");
        russian.setFindChatButton("Искать");
        russian.setFindChatDescription("Введите какое то слово, чтобы найти чат");
        russian.setCreatePrivateChat("Приватный");
        russian.setCreateGroup("Группа");
        russian.setCreateChannel("Канал");
        russian.setCreateChatButton("Создать");
        russian.setTranslateMessageButton("Перевести");
        russian.setInputMessagePlaceholder("Введите сообщение здесь...");
        russian.setSendMessageButton("Отправить!");
        russian.setSubscribers("подписчики");
        russian.setGroupMembers("участники");
        russian.setChangeLanguage("Изменить");
        russian.setToSendOfUser("Написать");
        russian.setSelectInterlocutorLabel("Выберите собеседника");
        russian.setEnterGroupName("Введите название группы");
        russian.setEnterChannelName("Введите имя канала");

        LanguageOfApp german = new LanguageOfApp();
        german.setType("de");
        german.setMessengerHeader("Messenger");
        german.setProfile("Profil");
        german.setLogout("heraus...");
        german.setFindChat("Chat suchen");
        german.setFindChatButton("Suchen");
        german.setFindChatDescription("Geben Sie ein Wort ein, um den Chat zu finden");
        german.setCreatePrivateChat("Privat");
        german.setCreateGroup("Die Gruppe");
        german.setCreateChannel("Kanal");
        german.setCreateChatButton("Schaffen");
        german.setTranslateMessageButton("Übersetzen");
        german.setInputMessagePlaceholder("Geben Sie hier eine Nachricht ein...");
        german.setSendMessageButton("Senden!");
        german.setSubscribers("Abonnenten");
        german.setGroupMembers("die Teilnehmer");
        german.setChangeLanguage("Ändern");
        german.setToSendOfUser("Schreiben");
        german.setSelectInterlocutorLabel("Wählen Sie einen Gesprächspartner aus");
        german.setEnterGroupName("Geben Sie einen Gruppennamen ein");
        german.setEnterChannelName("Geben Sie einen Kanalnamen ein");

        LanguageOfApp chinese = new LanguageOfApp();
        chinese.setType("zh");
        chinese.setMessengerHeader("信使");
        chinese.setProfile("个人资料");
        chinese.setLogout("出口；出口");
        chinese.setFindChat("搜索聊天");
        chinese.setFindChatButton("搜索");
        chinese.setFindChatDescription("输入一些单词来查找聊天");
        chinese.setCreatePrivateChat("私人");
        chinese.setCreateGroup("团体");
        chinese.setCreateChannel("频道");
        chinese.setCreateChatButton("要创建");
        chinese.setTranslateMessageButton("翻译；翻译");
        chinese.setInputMessagePlaceholder("在这里输入消息。..");
        chinese.setSendMessageButton("发送!");
        chinese.setSubscribers("登记用户");
        chinese.setGroupMembers("参加者");
        chinese.setChangeLanguage("要改变");
        chinese.setToSendOfUser("要写");
        chinese.setSelectInterlocutorLabel("选择对话伙伴");
        chinese.setEnterGroupName("输入组的名称");
        chinese.setEnterChannelName("输入频道名称");

        LanguageOfApp italian = new LanguageOfApp();
        italian.setType("it");
        italian.setMessengerHeader("Messenger");
        italian.setProfile("profilo");
        italian.setLogout("uscire");
        italian.setFindChat("Cerca chat");
        italian.setFindChatButton("Cercare");
        italian.setFindChatDescription("Digita una parola per trovare la chat");
        italian.setCreatePrivateChat("Privato");
        italian.setCreateGroup("Gruppo");
        italian.setCreateChannel("Canale");
        italian.setCreateChatButton("Creare");
        italian.setTranslateMessageButton("Tradurre");
        italian.setInputMessagePlaceholder("Inserisci il messaggio qui...");
        italian.setSendMessageButton("Inviare!");
        italian.setSubscribers("sottoscrittori");
        italian.setGroupMembers("partecipanti");
        italian.setChangeLanguage("Modificare");
        italian.setToSendOfUser("Scrivere");
        italian.setSelectInterlocutorLabel("Scegli un interlocutore");
        italian.setEnterGroupName("Inserisci il nome del gruppo");
        italian.setEnterChannelName("Immettere il nome del canale");

        languageOfAppRepository.save(english);
        languageOfAppRepository.save(russian);
        languageOfAppRepository.save(german);
        languageOfAppRepository.save(chinese);
        languageOfAppRepository.save(italian);
    }
}
