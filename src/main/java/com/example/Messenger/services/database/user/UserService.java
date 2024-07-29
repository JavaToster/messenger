package com.example.Messenger.services.database.user;

import com.example.Messenger.DAO.user.MessengerUserDAO;
import com.example.Messenger.dto.user.InfoOfUserDTO;
import com.example.Messenger.dto.user.RegisterUserDTO;
import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.chat.PrivateChat;
import com.example.Messenger.models.message.ImageMessage;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.models.user.*;
import com.example.Messenger.repositories.database.user.*;
import com.example.Messenger.repositories.database.chat.ChatRepository;
import com.example.Messenger.services.database.SettingsOfUserService;
import com.example.Messenger.services.email.SendRestoreCodeToEmailService;
import com.example.Messenger.balancers.TranslateBalancer;
import com.example.Messenger.util.enums.ChatMemberType;
import com.example.Messenger.util.enums.LanguageType;
import com.example.Messenger.util.enums.RoleOfUser;
import com.example.Messenger.util.enums.StatusOfEqualsCodes;
import com.example.Messenger.util.exceptions.ChatNotFoundException;
import com.example.Messenger.util.exceptions.UserNotFoundException;
import com.example.Messenger.util.threads.DeleteRestoreCodeThread;
import com.example.Messenger.util.threads.ReBlockUserThread;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
    private final ComplaintOfUserRepository complaintOfUserRepository;
    private final SettingsOfUserService settingsOfUserService;
    private final IconOfUserService iconOfUserService;
    private final MessengerUserDAO messengerUserDAO;
    @Value("${image.path.user.icons}")
    private String imagePath;

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

    public static List<Chat> FIND_CHATS_BY_USERNAME(User user) {
        List<Chat> userChats = new ArrayList<>();

        List<ChatMember> chatMembers = user.getMembers();
        chatMembers.forEach(chatMember -> userChats.add(chatMember.getChat()));
        return userChats;
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

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    public User register(RegisterUserDTO registerUserDTO) throws RuntimeException, IOException{
        System.out.println("I was registered");
        User user = userRepository.save(new User(registerUserDTO));
        loadBalancer.add(user.getId());
        settingsOfUserService.create(registerUserDTO, user);
        iconOfUserService.createNewIcon(registerUserDTO.getIcon(), user);
        return user;
    }

    public User findById(int id){
        return userRepository.findById(id).orElse(null);
    }


    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Cacheable(value = "userInfoByUsername", key = "#username")
    public InfoOfUserDTO findUserInfoByUsername(String username, String myUsername){
        return convertToUserDTO(getUser(username), myUsername);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findWithout(String username){
        List<User> users = userRepository.findAll();
        users.remove(findByUsername(username));

        return users;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void setLastOnline(String name) {
        User user = userRepository.findByUsername(name).orElse(null);
        user.setLastOnlineTime(new Date());
        userRepository.save(user);
    }


    private static String addZeroToTime(int time){
        return time<10? "0"+time: ""+time;
    }

    public String getLastOnlineTime(String username){
        if(messengerUserService.findByUsername(username).getClass() == Bot.class){
            return "bot";
        }

        Calendar calendarOfUser = Calendar.getInstance();
        Calendar calendarNow = Calendar.getInstance();

        Date dateOfUser = userRepository.findByUsername(username).orElse(null).getLastOnlineTime();
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

    public boolean isBan(String username, int chatId){
        List<ChatMember> banMembers = chatRepository.findById(chatId).orElseThrow(ChatNotFoundException::new).getMembers();
        for(ChatMember chatMember: banMembers){
            if(chatMember.getUser().equals(username) && chatMember.getMemberType() == ChatMemberType.BLOCK){
                return true;
            }
        }
        return false;
    }

    private static String getNameOfYear(int differenceOfYear){
        if(differenceOfYear == 1){
            return "год";
        }else{
            return differenceOfYear<5 ? "года" : "лет";
        }
    }

    private static String getNameOfMonth(int differenceOfMonth){
        return differenceOfMonth == 1 ? "месяц" : "месяцев";
    }
    private static String getNameOfDay(int differenceOfDay){
        return differenceOfDay < 5 ? "дня" : "дней";
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void block(int id, int chatId) {
        ChatMember chatMember = chatMemberRepository.findByUserAndChat(userRepository.findById(id).orElseThrow(UserNotFoundException::new), chatRepository.findById(chatId).orElseThrow(ChatNotFoundException::new)).orElse(null);
        chatMember.setMemberType(ChatMemberType.BLOCK);
        chatMemberRepository.save(chatMember);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void unblock(int id, int chatId) {
        ChatMember chatMember = chatMemberRepository.findByUserAndChat(userRepository.findById(id).orElse(null), chatRepository.findById(chatId).orElse(null)).orElse(null);
        chatMember.setMemberType(ChatMemberType.MEMBER);
        chatMemberRepository.save(chatMember);
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
        DeleteRestoreCodeThread deleteRestoreCodeThread = new DeleteRestoreCodeThread(email, sendRestoreCodeToEmailService);
        UserService.setCookie(response, "restoreEmail", email, 120);
        deleteRestoreCodeThread.start();
    }

    public StatusOfEqualsCodes checkRestoreCode(String email, int code){
        return sendRestoreCodeToEmailService.checkCode(email, code);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void changePasswordByEmail(String email, String password) {
        User user = userRepository.findByEmail(email).orElse(null);
        user.setPassword(encoder.encode(password));
        userRepository.save(user);
        removeEmailFromRestoreBalancer(email);
    }

    private void removeEmailFromRestoreBalancer(String email) {
        sendRestoreCodeToEmailService.removeEmailFromBalancer(email);
    }

    // метод для получения изображений из приватного чата
    // используется в окне просмотра пользователя

    public List<String> getImagesListByInterlocutors(String username, String myUsername) {
        List<ChatMember> membersList1 = chatMemberRepository.findByUser(getUser(username));
        List<ChatMember> membersList2 = chatMemberRepository.findByUser(getUser(myUsername));

        List<String> urls = new LinkedList<>();

        for(ChatMember member: membersList1){
            Chat chatOfMember = member.getChat();
            if(chatOfMember.getClass() != PrivateChat.class){
                continue;
            }

            for(ChatMember member2: membersList2){
                if(chatOfMember.getMembers().contains(member2)){
                    List<String> urlsByChat = getImagesFromChat(chatOfMember);
                    urls.addAll(urlsByChat);
                }
            }
        }
        return urls;
    }
    private List<String> getImagesFromChat(Chat chat){
        List<String> urlsOfImages = new LinkedList<>();

        for(MessageWrapper message: chat.getMessages()){
            if(message.getClass() == ImageMessage.class){
                urlsOfImages.add(message.getContent());
            }
        }
        return urlsOfImages;
    }

    private User getUser(String username){
        return userRepository.findByUsername(username).orElse(null);
    }

    private User getUser(int id){
        return userRepository.findById(id).orElse(null);
    }

    public List<ComplaintOfUser> getComplaintsOfUser(User user) {
        return complaintOfUserRepository.findByOwner(user);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void banComplaintsUser() {
        List<User> users = userRepository.findAll();
        for(User user: users){
            if(user.getRole().equals(RoleOfUser.ROLE_BAN)){
                continue;
            }
            if(user.getComplaints() != null){
                if(user.getComplaints().size() >= 3){
                    user.setRole(RoleOfUser.ROLE_BAN);
                    userRepository.save(user);
                }
            }
        }
    }

    public List<User> getUsersWithComplaint() {
        List<User> users = findAll();
        List<User> complaintUsers = new LinkedList<>();
        for(User user: users){
            if(user.getComplaints() != null || user.getComplaints().size() > 0){
                complaintUsers.add(user);
            }
        }
        return complaintUsers;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    public void banUser(String username, String time) {
        Calendar calendar = Calendar.getInstance();
        switch (time){
            case "12h" -> calendar.add(Calendar.SECOND, 30);
            case "24h" -> calendar.add(Calendar.HOUR, 24);
            case "14d" -> calendar.add(Calendar.DAY_OF_MONTH, 14);
            case "30d" -> calendar.add(Calendar.DAY_OF_MONTH, 30);
            case "120d" -> calendar.add(Calendar.DAY_OF_MONTH, 120);
            case "0" -> calendar = null;
            default -> calendar.add(Calendar.MINUTE, 0);
        }

        User user = getUser(username);
        ReBlockUserThread reBlockUserThread = new ReBlockUserThread(user, (this), calendar);
        reBlockUserThread.start();

        user.setRole(RoleOfUser.ROLE_BAN);
        userRepository.save(user);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    public synchronized void unban(User user) {
        user.setRole(RoleOfUser.ROLE_USER);
        userRepository.save(user);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void setBlocker(String username) {
        User user = getUser(username);
        user.setRole(RoleOfUser.ROLE_BLOCKER);
        userRepository.save(user);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void unsetBlocker(String username){
        User user = getUser(username);
        user.setRole(RoleOfUser.ROLE_USER);
        userRepository.save(user);
    }

    public LanguageType getUserLanguageMode(String username) {
        return getUser(username).getSettingsOfUser().getLang();
    }

    public SettingsOfUser getSettings(int userId) {
        return getUser(userId).getSettingsOfUser();
    }

    private InfoOfUserDTO convertToUserDTO(User user, String myUsername) {
        InfoOfUserDTO info = new InfoOfUserDTO(user.getId(), user.getUsername(), user.getName(), user.getLastname(), user.getEmail(), user.getLinkOfIcon());
        info.setLastTime(messengerUserDAO.getLastOnlineTimeAsString(user));
        info.setImagesUrl(getImagesListByInterlocutors(user.getUsername(), myUsername));
        return info;
    }

    public User initializeUserById(int id){
        return getUser(id);
    }
}
