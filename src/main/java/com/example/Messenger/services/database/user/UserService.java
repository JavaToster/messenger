package com.example.Messenger.services.database.user;

import com.example.Messenger.DAO.chat.ChatDAO;
import com.example.Messenger.DAO.user.ChatMemberDAO;
import com.example.Messenger.DAO.user.ComplaintOfUserDAO;
import com.example.Messenger.DAO.user.MessengerUserDAO;
import com.example.Messenger.DAO.user.UserDAO;
import com.example.Messenger.dto.auth.AuthDTO;
import com.example.Messenger.dto.auth.ForgotPasswordDTO;
import com.example.Messenger.dto.auth.NewPasswordDTO;
import com.example.Messenger.dto.user.UserDTO;
import com.example.Messenger.dto.user.RegisterUserDTO;
import com.example.Messenger.exceptions.ValidateException;
import com.example.Messenger.exceptions.auth.AuthException;
import com.example.Messenger.exceptions.auth.RegistrationException;
import com.example.Messenger.exceptions.security.ForbiddenException;
import com.example.Messenger.models.chat.Chat;
import com.example.Messenger.models.chat.PrivateChat;
import com.example.Messenger.models.message.ImageMessage;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.models.user.*;
import com.example.Messenger.security.JwtUtil;
import com.example.Messenger.services.database.SettingsOfUserService;
import com.example.Messenger.services.email.SendRestoreCodeToEmailService;
import com.example.Messenger.balancers.TranslateBalancer;
import com.example.Messenger.util.ErrorMessageCreator;
import com.example.Messenger.util.enums.ChatMemberType;
import com.example.Messenger.util.enums.RoleOfUser;
import com.example.Messenger.util.enums.StatusOfEqualsCodes;
import com.example.Messenger.exceptions.user.UserNotFoundException;
import com.example.Messenger.util.threads.DeleteRestoreCodeThread;
import com.example.Messenger.util.threads.ReBlockUserThread;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final ChatDAO chatDAO;
    private final UserDAO userDAO;
    private final TranslateBalancer loadBalancer;
    private final MessengerUserService messengerUserService;
    private final SendRestoreCodeToEmailService sendRestoreCodeToEmailService;
    private final PasswordEncoder encoder;
    private final SettingsOfUserService settingsOfUserService;
    private final MessengerUserDAO messengerUserDAO;
    private final ChatMemberDAO chatMemberDAO;
    private final ComplaintOfUserDAO complaintOfUserDAO;
    private final PasswordEncoder passwordEncoder;
    private final ErrorMessageCreator errorMessageCreator;
    private final JwtUtil jwtUtil;

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

    public List<Chat> findChatOfUser(String username) {
        User user = findByUsername(username);
        List<Chat> chatsOfUser = new ArrayList<>();
        user.getMembers().forEach(chatMember -> chatsOfUser.add(chatMember.getChat()));
        return chatsOfUser;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        try {
            user = userDAO.findByUsername(username);
            return new com.example.Messenger.security.UserDetails(user);
        }catch (UserNotFoundException e){
            try{
                user = userDAO.findByEmail(username);
                return new com.example.Messenger.security.UserDetails(user);
            }catch (UserNotFoundException g){
                throw new AuthException("Not correct authentication data");
            }
        }
    }
    public Cookie createCookie(String username, String value, int age){
        Cookie cookie = new Cookie(username, value);
        cookie.setMaxAge(age);

        return cookie;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    public void register(RegisterUserDTO registerUserDTO, BindingResult errors) throws RuntimeException{
        if(errors.hasErrors()){
            String msg = errorMessageCreator.createErrorMessage(errors);
            throw new ValidateException(msg);
        }


        System.out.println(registerUserDTO.getPassword());
        String encodedPassword = passwordEncoder.encode(registerUserDTO.getPassword());
        registerUserDTO.setPassword(encodedPassword);
        User user = userDAO.save(new User(registerUserDTO));
//        loadBalancer.add(user.getId());
        settingsOfUserService.create(registerUserDTO, user);
    }

    public User findById(int id){
        return userDAO.findById(id);
    }


    public User findByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    @Cacheable(value = "userInfoByUsername", key = "#username")
    public UserDTO findUserInfoByUsername(String username, String myUsername){
        return convertToUserDTO(userDAO.findByUsername(username), myUsername);
    }

    public List<User> findAll(){
        return userDAO.findAll();
    }

    public List<User> findWithout(String username){
        List<User> users = userDAO.findAll();
        users.remove(findByUsername(username));

        return users;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void setLastOnline(String name) {
        User user = userDAO.findByUsername(name);
        user.setLastOnlineTime(new Date());
        userDAO.save(user);
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

        Date dateOfUser = userDAO.findByUsername(username).getLastOnlineTime();
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
        List<ChatMember> banMembers = chatDAO.findById(chatId).getMembers();
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
    public void block(int userId, int chatId, String blockerUsername) {
        if(!(chatMemberDAO.findByChatAndUser(chatId, blockerUsername).getMemberType() == ChatMemberType.ADMIN)){
            if(!(chatMemberDAO.findByChatAndUser(chatId, blockerUsername).getMemberType() == ChatMemberType.OWNER)){
                throw new ForbiddenException("You can't block this user, because you are not a admin or owner a this group");
            }
        }

        ChatMember chatMember = chatMemberDAO.findByChatAndUser(chatId, userId);
        chatMember.setMemberType(ChatMemberType.BLOCK);
        chatMemberDAO.save(chatMember);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void unblock(int userId, int chatId, String blockerUsername) {
        if(!(chatMemberDAO.findByChatAndUser(chatId, blockerUsername).getMemberType() == ChatMemberType.ADMIN)){
            if(!(chatMemberDAO.findByChatAndUser(chatId, blockerUsername).getMemberType() == ChatMemberType.OWNER)){
                throw new ForbiddenException("You can't unblock this user, because you are not a admin or owner a this group");
            }
        }

        ChatMember chatMember = chatMemberDAO.findByChatAndUser(userId, chatId);
        chatMember.setMemberType(ChatMemberType.MEMBER);
        chatMemberDAO.save(chatMember);
    }

    public void sendCodeToRestore(ForgotPasswordDTO forgotPasswordDTO, BindingResult errors) {
        if(errors.hasFieldErrors("email")){
            String msg = errorMessageCreator.createErrorMessageByField(errors, "email");
            throw new ValidateException(msg);
        }

        sendRestoreCodeToEmailService.sendCode(forgotPasswordDTO.getEmail());
        DeleteRestoreCodeThread deleteRestoreCodeThread = new DeleteRestoreCodeThread(forgotPasswordDTO.getEmail(), sendRestoreCodeToEmailService);
        deleteRestoreCodeThread.start();
    }

    public StatusOfEqualsCodes checkRestoreCode(ForgotPasswordDTO data, BindingResult errors){
        if(errors.hasErrors()){
            String msg = errorMessageCreator.createErrorMessage(errors);
            throw new ValidateException(msg);
        }
        return sendRestoreCodeToEmailService.checkCode(data.getEmail(), data.getCode());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void changePasswordByEmail(NewPasswordDTO newPasswordDTO, BindingResult errors) {
        if(errors.hasErrors()){
            throw new ValidateException(errorMessageCreator.createErrorMessage(errors));

        }
        User user = userDAO.findByEmail(newPasswordDTO.getEmail());
        user.setPassword(encoder.encode(newPasswordDTO.getPassword()));
        userDAO.save(user);
        removeEmailFromRestoreBalancer(newPasswordDTO.getEmail());
    }

    private void removeEmailFromRestoreBalancer(String email) {
        sendRestoreCodeToEmailService.removeEmailFromBalancer(email);
    }

    // метод для получения изображений из приватного чата
    // используется в окне просмотра пользователя

    public List<String> getImagesListByInterlocutors(String interlocutorUsername, String viewerUsername) {
        List<ChatMember> membersList1 = chatMemberDAO.findByUser(interlocutorUsername);
        List<ChatMember> membersList2 = chatMemberDAO.findByUser(viewerUsername);

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


    public List<ComplaintOfUser> getComplaintsOfUser(User user) {
        return complaintOfUserDAO.findByOwner(user);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void banComplaintsUser() {
        List<User> users = userDAO.findAll();
        for(User user: users){
            if(user.getRole().equals(RoleOfUser.ROLE_BAN)){
                continue;
            }
            if(user.getComplaints() != null){
                if(user.getComplaints().size() >= 3){
                    user.setRole(RoleOfUser.ROLE_BAN);
                    userDAO.save(user);
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

        User user = userDAO.findByUsername(username);
        ReBlockUserThread reBlockUserThread = new ReBlockUserThread(user, (this), calendar);
        reBlockUserThread.start();

        user.setRole(RoleOfUser.ROLE_BAN);
        userDAO.save(user);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    public synchronized void unban(User user) {
        user.setRole(RoleOfUser.ROLE_USER);
        userDAO.save(user);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void setBlocker(String username) {
        User user = userDAO.findByUsername(username);
        user.setRole(RoleOfUser.ROLE_BLOCKER);
        userDAO.save(user);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void unsetBlocker(String username){
        User user = userDAO.findByUsername(username);
        user.setRole(RoleOfUser.ROLE_USER);
        userDAO.save(user);
    }

    public SettingsOfUser getSettings(int userId) {
        return userDAO.findById(userId).getSettingsOfUser();
    }

    private UserDTO convertToUserDTO(User user, String myUsername) {
        List<String> linkOfImagesInnerChatOfUser = getImagesListByInterlocutors(user.getUsername(), myUsername);
        String lastTime = messengerUserDAO.getLastOnlineTimeAsString(user);

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .phone(user.getPhone())
                .firstname(user.getName())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .imagesUrl(linkOfImagesInnerChatOfUser)
                .lastTime(lastTime)
                .build();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void setAdmin(int userId, int channelId, String userSetterUsername) {
        if((!(chatMemberDAO.findByChatAndUser(channelId, userSetterUsername).getMemberType() == ChatMemberType.OWNER))){
            throw new ForbiddenException("You can't set admin this user, because you are not a owner of group");
        }

        ChatMember member = chatMemberDAO.findByChatAndUser(channelId, userId);

        member.setMemberType(ChatMemberType.ADMIN);
        chatMemberDAO.save(member);

    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void resetAdmin(int userId, int channelId, String userSetterUsername) {
        if((!(chatMemberDAO.findByChatAndUser(channelId, userSetterUsername).getMemberType() == ChatMemberType.OWNER))){
            throw new ForbiddenException("You can't reset admin this user, because you are not a owner of group");
        }
        MessengerUser user = messengerUserDAO.findById(userId);
        Chat chat = chatDAO.findById(channelId);

        // тут мы сразу получаем member потому что до этого он уже был назначем на должность админа, поэтому условие его проверки тут не требуется
        ChatMember member = ChatMemberService.findByChatAndUser(user, chat).get();

        member.setMemberType(ChatMemberType.MEMBER);
        chatMemberDAO.save(member);
    }

    public List<Chat> findCommonChats(String username, String name) {
        List<Chat> chats = chatDAO.findByUser(findByUsername(username));
        List<Chat> commonChats = new ArrayList<>();
        for (Chat chat: chats){
            if(chatMemberDAO.userIsMember(name, chat.getMembers())){
                commonChats.add(chat);
            }
        }
        return commonChats;
    }

    public String login(AuthDTO authDTO) {
        try {
            UserDetails userDetails = loadUserByUsername(authDTO.getLogin());
            if (passwordEncoder.matches(authDTO.getPassword(), userDetails.getPassword())) {
                return jwtUtil.generateToken(authDTO.getLogin());
            } else {
                throw new AuthException("Not correct authentication data ");
            }
        }catch (UsernameNotFoundException e){
            throw new AuthException("Not correct authentication data");
        }
    }
}
