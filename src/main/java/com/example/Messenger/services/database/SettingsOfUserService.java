package com.example.Messenger.services.database;

import com.example.Messenger.dto.user.RegisterUserDTO;
import com.example.Messenger.dto.util.TranslateModeDTO;
import com.example.Messenger.models.user.SettingsOfUser;
import com.example.Messenger.models.user.User;
import com.example.Messenger.repositories.database.user.SettingsOfUserRepository;
import com.example.Messenger.repositories.database.user.UserRepository;
import com.example.Messenger.util.enums.LanguageType;
import com.example.Messenger.exceptions.translate.LanguageNotSupportedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SettingsOfUserService {
    private final SettingsOfUserRepository settingsOfUserRepository;
    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void changeAppLanguage(int id, String lang) {
        SettingsOfUser settings = getUser(id).getSettingsOfUser();
        settings.setLang(convertToLang(lang));
        settingsOfUserRepository.save(settings);

    }

    private User getUser(int id){
        return userRepository.findById(id).orElse(null);
    }

    private LanguageType convertToLang(String lang){
        LanguageType type;
        switch (lang){
            case "en" -> type = LanguageType.ENGLISH;
            case "ru" -> type = LanguageType.RUSSIAN;
            case "zh" -> type = LanguageType.CHINESE;
            case "de" -> type = LanguageType.GERMAN;
            case "it" -> type = LanguageType.ITALIAN;
            default -> throw new LanguageNotSupportedException();
        }

        return type;
    }

    @Transactional
    public void create(RegisterUserDTO registerUserDTO, User user) {
        SettingsOfUser settingsOfUser = new SettingsOfUser();
        settingsOfUser.setLang(LanguageType.valueOf(registerUserDTO.getLang()));
        settingsOfUser.setTranslateMessageMode("en|ru");
        settingsOfUser.setOwner(user);
        settingsOfUserRepository.save(settingsOfUser);

    }

    @Transactional
    public void changeUserTranslateMessageLangMode(TranslateModeDTO translateModeDTO) {
        SettingsOfUser settings = getUser(translateModeDTO.getUserId()).getSettingsOfUser();
        settings.setTranslateMessageMode(String.join("|", translateModeDTO.getFrom(), translateModeDTO.getTo()));
        settingsOfUserRepository.save(settings);
    }
}
