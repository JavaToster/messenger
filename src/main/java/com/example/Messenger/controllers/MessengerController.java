package com.example.Messenger.controllers;

import com.example.Messenger.dto.ChatDTO;
import com.example.Messenger.dto.chat.InfoOfChatDTO;

import com.example.Messenger.dto.message.BlockMessageDTO;
import com.example.Messenger.dto.user.FoundUserOfUsername;
import com.example.Messenger.models.chat.*;
import com.example.Messenger.models.message.BlockMessage;
import com.example.Messenger.models.user.Bot;
import com.example.Messenger.models.user.ChatMember;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.security.UserDetails;
import com.example.Messenger.services.auth.AuthenticationService;
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

import java.util.List;

@Controller
@RequestMapping({"/messenger", "/messenger/", "/", ""})
@RequiredArgsConstructor
public class MessengerController {
    private final GroupChatService groupChatService;
    private final UserService userService;
    private final ChatService chatService;
    private final BlockMessageService blockMessageService;
    private final DeleteEmptyChatsThread deleteEmptyChatsThread;
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
    public String showChat(@CookieValue("username")String username, @PathVariable("id") int chatId, Model model){
        Chat chat = chatService.findById(chatId);
        if(userService.isBan(username, chat)){
            return "redirect:/messenger";
        }
        //когда окно чата откроется, то все сообщения собеседника пользователя будут изменены на "прочитано"
        messageWrapperService.messageWasBeRead(chatId, username);

        InfoOfChatDTO infoOfChatDTO = convertor.convertToInfoOfChatDTO(chat, username);

        model.addAttribute("infoOfChat", infoOfChatDTO);

        return chatService.getReturnedHtmlFile(chat);
    }

    @PostMapping("/chats/{id}/send-message")
    public String sendMessage(@RequestParam(value = "image") MultipartFile image, @RequestParam("text") String text, @RequestParam("user") int userId, @PathVariable("id") int chatId){
        messageWrapperService.send(image, chatId, userId, text);

        return "redirect:/messenger/chats/"+chatId;
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

//    @GetMapping({"/chats/{id}/show", "/chats/{id}/show/"})
//    public String showChatHeader(@CookieValue("username") String username, @PathVariable("id") int chatId, Model model){
//
//        if(chatService.findById(chatId).getClass() == GroupChat.class) {
//            if (!groupChatService.getGroupOwner(chatId).equals(username)) {
//                return "redirect:/messenger/chats/" + chatId;
//            }
//        }
//        Chat chat = chatService.findById(chatId);
//        model.addAttribute("chat", chat);
//
//        //remake check chat type -> channel, group, private
//        ChatHeadDTO chatHeadDTOList = convertor.convertToChatHeadDTO(chat, username);
//        if(chat.getClass() == PrivateChat.class){
//            chatHeadDTOList = messengerMapper.map(chat, username);
//            model.addAttribute("chatHead", chatHeadDTOList);
//            return "/html/chat/privateChatHead";
//        }else if(foundChat.getClass() == GroupChat.class){
//            model.addAttribute("channelMember", new GroupChatMemberDTO());
//            chatHeadDTOList = messengerMapper.map(foundChat);
//            model.addAttribute("chatHead", chatHeadDTOList);
//            return "/html/chat/groupChatHead";
//        }else if(foundChat.getClass() == Channel.class){
//            model.addAttribute("channelMember", new ChannelMemberDTO());
//            model.addAttribute("channelAdmin", new ChannelMemberDTO());
//            chatHeadDTOList = messengerMapper.map(foundChat);
//            model.addAttribute("chatHead", chatHeadDTOList);
//            return "/html/chat/channelChatHead";
//        }else{
//            chatHeadDTOList = messengerMapper.map(foundChat);
//            model.addAttribute("chatHead", chatHeadDTOList);
//            return "/html/chat/botChatHead";
//        }
//    }

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

    private void isMember(String username, Chat chat) {
        List<ChatMember> chatMembers = chat.getMembers();
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