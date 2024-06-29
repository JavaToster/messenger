package com.example.Messenger.controllers;

import com.example.Messenger.dto.ChatDTO;
import com.example.Messenger.dto.chatHead.ChatHeadDTO;
import com.example.Messenger.dto.chatHead.channel.ChannelMemberDTO;
import com.example.Messenger.dto.chatHead.group.GroupChatMemberDTO;
import com.example.Messenger.dto.message.BlockMessageDTO;
import com.example.Messenger.dto.user.FoundUserOfUsername;
import com.example.Messenger.dto.util.DateDayOfMessagesDTO;
import com.example.Messenger.models.chat.*;
import com.example.Messenger.models.message.BlockMessage;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.models.user.Bot;
import com.example.Messenger.models.user.ChatMember;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.security.UserDetails;
import com.example.Messenger.services.auth.AuthenticationService;
import com.example.Messenger.services.database.chat.BotChatService;
import com.example.Messenger.services.database.chat.ChannelService;
import com.example.Messenger.services.database.chat.ChatService;
import com.example.Messenger.services.database.chat.GroupChatService;
import com.example.Messenger.services.database.message.BlockMessageService;
import com.example.Messenger.services.database.message.MessageWrapperService;
import com.example.Messenger.services.email.redis.languageOfApp.LanguageOfAppService;
import com.example.Messenger.services.database.user.BotService;
import com.example.Messenger.services.database.user.MessengerUserService;
import com.example.Messenger.services.database.user.UserService;
import com.example.Messenger.util.Convertor;
import com.example.Messenger.balancers.BalancerOfFoundChats;
import com.example.Messenger.util.chat.UserFoundedChats;
import com.example.Messenger.util.threads.CheckComplaintsOfUserThread;
import com.example.Messenger.util.threads.DeleteEmptyChatsThread;
import com.example.Messenger.util.MessengerMapper;
import com.example.Messenger.util.exceptions.ChatNotFoundException;
import com.example.Messenger.util.exceptions.UserNotMemberException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping({"/messenger", "/messenger/", "/", ""})
@RequiredArgsConstructor
public class MessengerController {
    private final GroupChatService groupChatService;
    private final UserService userService;
    private final ChatService chatService;
    private final ChannelService channelService;
    private final BlockMessageService blockMessageService;
    private final DeleteEmptyChatsThread deleteEmptyChatsThread;
    private final MessengerMapper messengerMapper;
    private final BotChatService botChatService;
    private final MessengerUserService messengerUserService;
    private final BotService botService;
    private final BalancerOfFoundChats balancerOfFoundChats;
    private final LanguageOfAppService languageOfAppService;
    private final MessageWrapperService messageWrapperService;
    private final CheckComplaintsOfUserThread checkComplaintsOfUserThread;
    private final AuthenticationService authenticationService;
    @Value("${bot.father.database.id}")
    private int botFatherDatabaseId;
    private final Convertor convertor;

    /** main window
     * главное окно*/

    @PostConstruct
    public void initialize(){
        deleteEmptyChatsThread.start();
        checkComplaintsOfUserThread.start();
    }
    @GetMapping("")
    public String messengerWindow(Authentication authentication, HttpServletResponse response, Model model){
        //получаем user details и уже после используем его для получения никнейма
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        //сортируем чаты по последнему сообщению
        //то есть если у нас сообщение пришло только что, то этот чат поднимется на вверх(будет первым в списке)
        List<Chat> sortedChats = chatService.sortChatsByLastMessage(userDetails.getUsername());

        List<ChatDTO> chats = convertor.convertToChatDTO(sortedChats, userDetails.getUsername());

        authenticationService.setUserSpecification(userDetails.getUsername());

        model.addAttribute("language", languageOfAppService.getLanguage(userService.getUserLanguageMode(userDetails.getUsername())));
        model.addAttribute("chats", chats);
        model.addAttribute("chat", new Chat());
        model.addAttribute("foundedChat", new UserFoundedChats());
        model.addAttribute("foundedChat1", new UserFoundedChats());
        model.addAttribute("foundedChatsOfChatName", balancerOfFoundChats.userFoundedChats(userDetails.getUsername()).get("ChatName"));
        model.addAttribute("foundedChatsOfMessage", balancerOfFoundChats.userFoundedChats(userDetails.getUsername()).get("MessageText"));
        model.addAttribute("foundUsers", balancerOfFoundChats.foundUsers(userDetails.getUsername()));
        model.addAttribute("foundUser", new FoundUserOfUsername());
        response.addCookie(userService.createCookie("username", userDetails.getUsername(), 60*60));

        return "/html/Messenger";
    }

    //используется для получения сведения какой чат хочет создать пользователь
    @PostMapping("/chats/create/redirect")
    public String redirectToCreateChatWindow(@ModelAttribute("chat") Chat chat){
        if(chat.getId() == 1){
            return "redirect:/messenger/chats/create";
        }else if(chat.getId() == 2){
            return "redirect:/messenger/chats/create?type=group";
        }else if(chat.getId() == 3){
            return "redirect:/messenger/chats/create?type=channel";
        }else{
            return "redirect:/messenger/chats/create";
        }
    }

    //окно создания чатов
    @GetMapping("/chats/create")
    public String createChat(Model model, @RequestParam(value = "type", required = false) String type, @CookieValue("username") String username){
        model.addAttribute("username", username);

        //если в запросе не было указано тип чата, то по умолчанию возвращаем приватный чат
        if(type == null){
            model.addAttribute("users", messengerUserService.findWithout(username));
            model.addAttribute("user", new MessengerUser());
            return "/html/chat/createPrivateChat";
        }
        else if(type.equals("group")){
            model.addAttribute("users", userService.findWithout(username));
            model.addAttribute("user", new MessengerUser());
            return "/html/chat/createGroupChat";
        }
        else if(type.equals("channel")){
            model.addAttribute("users", userService.findWithout(username));
            model.addAttribute("user", new MessengerUser());
            return "/html/chat/createChannel";
        }
        //какие либо другие типы тоже возвращают приватный чат
        else{
            model.addAttribute("users", userService.findWithout(username));
            model.addAttribute("user", new MessengerUser());
            model.addAttribute("bots", botService.findAll());
            model.addAttribute("bot", new Bot());
            return "/html/chat/createPrivateChat";
        }
    }

    //для создания приватного чата, внутри сервиса будет определено какой чат хочет создать пользователь(по типу 1 аргумента, если Messenger user будет ботом, то создастся чат с ботом, а если человек, то обычный приватный чат)
    @PostMapping("/chats/create-chat-private-or-bot")
    public String createPrivateChat(@ModelAttribute("user") MessengerUser user, @RequestParam("username") String username){
        // в ModelAttribute приходит объект только с not empty полем id поэтому стоит его инициализировать
        user = userService.initializeUserById(user.getId());
        int id = chatService.createPrivateOrBotChat(user, username);

        return "redirect:/messenger/chats/"+id;
    }

    //окно просмотра чата
    @GetMapping("/chats/{id}")
    public String showChat(@CookieValue("username")String username, @PathVariable("id") int chatId, Model model, @RequestParam(value = "type", required = false) String type){
        //если пользователь в этом чате заблокирован, то редирект на главную страницу
        if(userService.isBan(username, chatService.findById(chatId))){
            return "redirect:/messenger";
        }
        //когда окно чата откроется, то все сообщения собеседника пользователя будут изменены на "прочитано"
        messageWrapperService.messageWasBeRead(chatId, username);

        List<MessageWrapper> messages;
        //если во время этого try/catch не вылезли исключения, значит все прошло успешно
        try {
            messages = messageWrapperService.findByChat(chatId);
            isMember(username, chatId);
        }catch (ChatNotFoundException | UserNotMemberException e) {
            return "redirect:/messenger";
        }


        Chat chat = chatService.findById(chatId);
        /**
         * определение заголовка чата
         * второй аргумент username нужен чтобы на его основе выводить определенные данные для пользователя
         */
        String chatTitle;
        String chatHeader;
        String returnedView;
        try {
            chatTitle = chatService.getChatTitle(chat, username);
            returnedView = chatService.getReturnedHtmlFile(chat);
            chatHeader = chatService.getChatHeader(chat, username);
        }catch (ChatNotFoundException e){
            return "redirect:/messenger";
        }

        List<DateDayOfMessagesDTO> messagesDateDTO;
        try {
            /** новый тип dto который предоставляет возможность собирать сообщения в группы
             * то есть, сообщения одного дня будут отделены от другого дня при помощи их даты отправки*/
            messagesDateDTO = convertor.convertToMessageDayDTO(messages, username);
        }catch (NoSuchElementException e){
            messagesDateDTO = Collections.emptyList();
            model.addAttribute("noMessages", true);
        }

        model.addAttribute("chatId", chatId);
        model.addAttribute("chatTitle", chatTitle);
        model.addAttribute("userId", userService.findByUsername(username).getId());
        model.addAttribute("chatHeader", chatHeader);
        model.addAttribute("sendingMessage", new MessageWrapper());
        model.addAttribute("language", languageOfAppService.getLanguage(userService.findByUsername(username).getLang()));
        model.addAttribute("forwardChats", chatService.getForwardChats(username));
        model.addAttribute("forwardChat", new ChatDTO());
        model.addAttribute("isPrivate", chat.getClass()== PrivateChat.class);
        model.addAttribute("message", new MessageWrapper());
        model.addAttribute("messageDateDTO", messagesDateDTO);
        model.addAttribute("messageDate", new DateDayOfMessagesDTO());
        model.addAttribute("noMessages", false);

        return returnedView;
    }

    //основной энд поинт чтобы отправить сообщение
    @PostMapping("/chats/{id}/send-message")
    public String sendMessage(@RequestParam(value = "image") MultipartFile image, @RequestParam("text") String text, @RequestParam("user") int userId, @PathVariable("id") int id){
        /** if user want to send a photo with text or only photo, we check, if image is empty
         * if multipart file is present → user want to send an image.
         * если пользователь хочет отправить фото с текстом или только фото, мы проверяем, если изображение пустое
         * если изображение имеется → человек хочет отправить изображение
        */
        if(!image.isEmpty()){
            messageWrapperService.sendPhoto(image, id, userId, text);
            return "redirect:/messenger/chats/"+id;
        }

        // if user want to send only text
        messageWrapperService.sendNotImage(text, id, userId);
        return "redirect:/messenger/chats/"+id;
    }

    // энд поинт для проверки блокированных сообщений
    // TODO ИЗМЕНИТЬ ДЛЯ НОВОГО ТИПА СООБЩЕНИЙ
    @GetMapping("/chats/{id}/block-messages")
    public String showBlockMessages(@PathVariable("id") int chatId, Model model, @CookieValue("username") String username){
        List<BlockMessageDTO> blockMessageDTOList = convertor.convertToChatDTOOfBlockMessage(blockMessageService.findByChat(chatId), username);

        model.addAttribute("chatId", chatId);
        model.addAttribute("blockMessage", new BlockMessage());
        model.addAttribute("blockMessages", blockMessageDTOList);

        return "/html/message/blockMessageAdd";
    }

    // TODO ИЗМЕНИТЬ ДЛЯ НОВОГО ТИПА СООБЩЕНИЙ
    @PostMapping("/chats/{id}/block-messages/add")
    public String addBlockMessage(@ModelAttribute("blockMessage") BlockMessage blockMessage, @PathVariable("id") int chatId){
        //проверка для того, что нету ли уже такого текста в списке блокированных сообщений
        if(blockMessageService.isBlockMessage(blockMessage, chatId)){
            return "redirect:/messenger/chats/"+chatId+"/block-messages";
        }

        //назначаем чат для блокированного сообщения
        blockMessage.setChat(chatService.findById(chatId));
        blockMessageService.add(blockMessage);

        return "redirect:/messenger/chats/"+chatId+"/block-messages";
    }

    // TODO ИЗМЕНИТЬ ДЛЯ НОВОГО ТИПА СООБЩЕНИЙ
    @PostMapping("/chats/{id}/block-messages/remove-block-message")
    public String removeBlockMessage(@RequestParam("messageId") int messageId, @PathVariable("id") int id){
        blockMessageService.remove(messageId);

        return "redirect:/messenger/chats/"+id+"/block-messages";
    }

    @GetMapping({"/chats/{id}/show", "/chats/{id}/show/"})
    public String showChatHeader(@CookieValue("username") String username, @PathVariable("id") int chatId, Model model){

        if(chatService.findById(chatId).getClass() == GroupChat.class) {
            if (!groupChatService.getGroupOwner(chatId).equals(username)) {
                return "redirect:/messenger/chats/" + chatId;
            }
        }
//        }else if(chatService.findById(chatId).getClass() == Channel.class){
//            if(!channelService.getChannelOwner(chatId).equals(username)){
//                return "redirect:/messenger/chats/"+chatId;
//            }
//        }
        model.addAttribute("chat", chatService.findById(chatId));

        //remake check chat type -> channel, group, private
        ChatHeadDTO chatHeadDTOList;
        Chat foundChat = chatService.findById(chatId);
        if(foundChat.getClass() == PrivateChat.class){
            chatHeadDTOList = messengerMapper.map(foundChat, username);
            model.addAttribute("chatHead", chatHeadDTOList);
            return "/html/chat/privateChatHead";
        }else if(foundChat.getClass() == GroupChat.class){
            model.addAttribute("channelMember", new GroupChatMemberDTO());
            chatHeadDTOList = messengerMapper.map(foundChat);
            model.addAttribute("chatHead", chatHeadDTOList);
            return "/html/chat/groupChatHead";
        }else if(foundChat.getClass() == Channel.class){
            model.addAttribute("channelMember", new ChannelMemberDTO());
            model.addAttribute("channelAdmin", new ChannelMemberDTO());
            chatHeadDTOList = messengerMapper.map(foundChat);
            model.addAttribute("chatHead", chatHeadDTOList);
            return "/html/chat/channelChatHead";
        }else{
            chatHeadDTOList = messengerMapper.map(foundChat);
            model.addAttribute("chatHead", chatHeadDTOList);
            return "/html/chat/botChatHead";
        }
    }

    @PostMapping("/chats/{id}/show/block-user")
    public String blockUser(@RequestParam("user_id") int id, @PathVariable("id") int chatId){
        userService.block(id, chatId);

        return "redirect:/messenger/chats/"+chatId+"/show/";
    }

    @PostMapping("/chats/{id}/show/unblock-user")
    public String unBlockUser(@RequestParam("user_id") int id, @PathVariable("id") int chatId){
        userService.unblock(id, chatId);

        return "redirect:/messenger/chats/"+chatId+"/show";
    }

    @PostMapping("/chats/{id}/show/set-admin")
    public String setAdmin(@RequestParam("user_id") int userId, @PathVariable("id") int chatId){
        messengerUserService.setAdmin(userId, chatId);

        return "redirect:/messenger/chats/"+chatId+"/show";
    }

    @PostMapping("/chats/{id}/show/reset-admin")
    public String resetAdmin(@RequestParam("user_id") int userId, @PathVariable("id") int chatId){
        messengerUserService.resetAdmin(userId, chatId);

        return "redirect:/messenger/chats/"+chatId+"/show";
    }

    @GetMapping("/translate")
    public String translateText(@RequestParam("text") String text, @CookieValue("username") String username){
        return "redirect:/messenger";
    }

    private void isMember(String username, int chatId) {
        List<ChatMember> chatMembers = chatService.findById(chatId).getMembers();
        for (ChatMember chatMember : chatMembers) {
            if ((chatMember.getUser()).getUsername().equals(username)) {
                return;
            }
        }
        throw new UserNotMemberException();
    }

    @GetMapping("/findChat")
    public String findChats(@RequestParam("findText") String findText, @CookieValue("username") String username) {
        List<ChatDTO> foundChatsByChatName = chatService.findChatsBySearchTextByChatName(findText, username);
        List<ChatDTO> foundChatsByMessage = chatService.findChatsBySearchTextByMessages(findText, username);
        List<FoundUserOfUsername> foundUsers = chatService.findUsersOfUsername(findText, username);

        balancerOfFoundChats.addFoundedChats(username, foundChatsByChatName, foundChatsByMessage, foundUsers);
        return "redirect:/messenger";
    }
}