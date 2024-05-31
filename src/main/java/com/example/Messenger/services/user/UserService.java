package com.example.Messenger.services.user;

import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.user.Bot;
import com.example.Messenger.models.user.ChatMember;
import com.example.Messenger.models.user.User;
import com.example.Messenger.repositories.user.ChatMemberRepository;
import com.example.Messenger.repositories.chat.ChatRepository;
import com.example.Messenger.repositories.user.UserRepository;
import com.example.Messenger.services.email.SendRestoreCodeToEmailService;
import com.example.Messenger.util.balancer.TranslateBalancer;
import com.example.Messenger.util.balancer.UserStatusBalancer;
import com.example.Messenger.util.enums.ChatMemberType;
import com.example.Messenger.util.enums.LanguageType;
import com.example.Messenger.util.enums.StatusOfEqualsCodes;
import com.example.Messenger.util.exceptions.LanguageNotSupportedException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final TranslateBalancer loadBalancer;
    private final MessengerUserService messengerUserService;
    private final SendRestoreCodeToEmailService sendRestoreCodeToEmailService;
    private final PasswordEncoder encoder;
    private final UserStatusBalancer statusBalancer;

    public static void setCookie(HttpServletResponse response, String name, String value, int age){
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(age);
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletResponse response, String name){
        Cookie cookie = new Cookie(name, "value");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletResponse response, String... names){
        for(String name: names){
            Cookie cookie = new Cookie(name, "value");
            response.addCookie(cookie);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> person  = userRepository.findByUsername(username);

        if(person.isEmpty()){
            throw new UsernameNotFoundException("User not found!");
        }

        return new com.example.Messenger.security.UserDetails(person.get());
    }

    public Cookie createCookie(String username, String value, int age){
        Cookie cookie = new Cookie(username, value);
        cookie.setMaxAge(age);

        return cookie;
    }

    @Transactional
    public void register(User user) {
        loadBalancer.add(userRepository.save(user).getId());
    }

    public User findById(int id){
        return userRepository.findById(id).orElse(null);
    }

    public List<Chat> findChatsByUsername(String username) {
        List<Chat> userChats = new ArrayList<>();

        List<ChatMember> chatMembers = userRepository.findByUsername(username).orElse(null).getMembers();
        chatMembers.forEach(chatMember -> userChats.add(chatMember.getChat()));
        return userChats;
    }


    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findWithout(String username){
        List<User> users = userRepository.findAll();
        users.remove(findByUsername(username));

        return users;
    }

    @Transactional
    public void setLastOnline(String name) {
        User user = userRepository.findByUsername(name).orElse(null);
        user.setLastOnline(new Date());
        userRepository.save(user);
    }


    private String addZeroToTime(int time){
        return time<10? "0"+time: ""+time;
    }

    public String getLastOnlineTime(String username){
        if(messengerUserService.findByUsername(username).getClass() == Bot.class){
            return "bot";
        }
        Calendar calendarOfUser = Calendar.getInstance();
        Calendar calendarNow = Calendar.getInstance();

        Date dateOfUser = userRepository.findByUsername(username).orElse(null).getLastOnline();
        Date dateNow = new Date();

        calendarOfUser.setTime(dateOfUser);
        calendarNow.setTime(dateNow);

        if(dateOfUser == null){
            return "Был(а) недавно";
        }


        int yearNow = calendarNow.get(Calendar.YEAR);
        int monthNow = calendarNow.get(Calendar.MONTH);
        int dayNow = calendarNow.get(Calendar.DAY_OF_MONTH);
        int yearOfUser = calendarOfUser.get(Calendar.YEAR);
        int monthOfUser = calendarOfUser.get(Calendar.MONTH);
        int dayOfUser = calendarOfUser.get(Calendar.DAY_OF_MONTH);

        if(yearNow == yearOfUser && monthNow == monthOfUser && dayNow == dayOfUser && dateNow.getHours() == dateOfUser.getHours() && dateNow.getMinutes() == dateOfUser.getMinutes()){
            return "в cети";
        }

        if(yearNow > yearOfUser){
            return "Был(а) в сети "+(yearNow-yearOfUser)+" "+getNameOfYear(yearNow-yearOfUser)+" назад";
        }else{
            if(monthNow > monthOfUser){
                return "Был(а) в сети "+(monthNow - monthOfUser)+" "+getNameOfMonth(monthNow-monthOfUser)+" назад";
            } else if (dayNow > dayOfUser) {
                if(dayNow-dayOfUser == 1) {
                    return "Был(а) в сети вчера";
                }else{
                    return "Был(а) в сети "+(dayNow-dayOfUser)+" "+getNameOfDay(dayNow-dayOfUser)+" назад";
                }
            }else{
                return "Был(а) в сети в "+addZeroToTime(dateOfUser.getHours())+":"+addZeroToTime(dateOfUser.getMinutes());
            }
        }
    }

    public boolean isBan(String username, Chat chat){
        List<ChatMember> banMembers = chatRepository.findById(chat.getId()).orElse(null).getMembers();
        for(ChatMember chatMember: banMembers){
            System.out.println(username);
            System.out.println(chatMember.getUsernameOfUser());
            if(chatMember.getUser().equals(username) && chatMember.getMemberType() == ChatMemberType.BLOCK){
                System.out.println(1);
                return true;
            }
        }
        return false;
    }

    private String getNameOfYear(int differenceOfYear){
        if(differenceOfYear == 1){
            return "год";
        }else{
            return differenceOfYear<5 ? "года" : "лет";
        }
    }
    private String getNameOfMonth(int differenceOfMonth){
        return differenceOfMonth == 1 ? "месяц" : "месяцев";
    }
    private String getNameOfDay(int differenceOfDay){
        return differenceOfDay < 5 ? "дня" : "дней";
    }

    @Transactional
    public void block(int id, int chatId) {
        ChatMember chatMember = chatMemberRepository.findByUserAndChat(userRepository.findById(id).orElse(null), chatRepository.findById(chatId).orElse(null)).orElse(null);
        chatMember.setMemberType(ChatMemberType.BLOCK);
        chatMemberRepository.save(chatMember);
    }

    @Transactional
    public void unblock(int id, int chatId) {
        ChatMember chatMember = chatMemberRepository.findByUserAndChat(userRepository.findById(id).orElse(null), chatRepository.findById(chatId).orElse(null)).orElse(null);
        chatMember.setMemberType(ChatMemberType.MEMBER);
        chatMemberRepository.save(chatMember);
    }

    @Transactional
    public void changeLang(int userId, String lang){
        User user = userRepository.findById(userId).orElse(null);
        user.setLang(convertToLang(lang));
        userRepository.save(user);
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

    public boolean isUser(String username, String email) {
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isEmpty()){
            user = userRepository.findByEmail(email);
            if(user.isEmpty()){
                return false;
            }
        }
        return true;
    }

    public boolean isEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public void sendCodeToRestore(HttpServletResponse response, String email) {
        sendRestoreCodeToEmailService.sendCode(email);
        UserService.setCookie(response, "restoreEmail", email, 120);

    }

    public StatusOfEqualsCodes checkRestoreCode(String email, int code){
        return sendRestoreCodeToEmailService.checkCode(email, code);
    }

    @Transactional
    public void changePasswordByEmail(String email, String password) {
        User user = userRepository.findByEmail(email).orElse(null);
        user.setPassword(encoder.encode(password));
        userRepository.save(user);
        removeEmailFromRestoreBalancer(email);
    }

    private void removeEmailFromRestoreBalancer(String email) {
        sendRestoreCodeToEmailService.removeEmailFromBalancer(email);
    }
}
