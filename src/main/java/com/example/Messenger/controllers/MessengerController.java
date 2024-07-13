package com.example.Messenger.controllers;

import com.example.Messenger.DAO.chat.ChatDAO;
import com.example.Messenger.dto.chat.ChatDTO;

import com.example.Messenger.dto.message.BlockMessageDTO;
import com.example.Messenger.dto.user.FoundUserOfUsername;
import com.example.Messenger.models.chat.*;
import com.example.Messenger.models.message.BlockMessage;
import com.example.Messenger.models.user.Bot;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.security.UserDetails;
import com.example.Messenger.services.auth.AuthenticationService;
import com.example.Messenger.services.database.chat.ChatService;
import com.example.Messenger.services.database.message.BlockMessageService;
import com.example.Messenger.services.database.message.MessageWrapperService;
import com.example.Messenger.services.email.redis.languageOfApp.LanguageOfAppService;
import com.example.Messenger.services.database.user.BotService;
import com.example.Messenger.services.database.user.MessengerUserService;
import com.example.Messenger.services.database.user.UserService;
import com.example.Messenger.util.Convertor;
import com.example.Messenger.balancers.BalancerOfFoundChats;
import com.example.Messenger.util.threads.CheckComplaintsOfUserThread;
import com.example.Messenger.util.threads.DeleteEmptyChatsThread;
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
    private final UserService userService;
    private final ChatService chatService;
    private final BlockMessageService blockMessageService;
    private final DeleteEmptyChatsThread deleteEmptyChatsThread;
    private final MessengerUserService messengerUserService;
    private final BotService botService;
    private final BalancerOfFoundChats balancerOfFoundChats;
    private final MessageWrapperService messageWrapperService;
    private final CheckComplaintsOfUserThread checkComplaintsOfUserThread;
    private final AuthenticationService authenticationService;
    private final ChatDAO chatDAO;
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
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        authenticationService.setUserSpecification(userDetails.getUsername());

        model.addAttribute("infoOfMainWindow", convertor.convertToMainInfoDTO(userDetails.getUsername()));
        model.addAttribute("chat", new Chat());
        response.addCookie(userService.createCookie("username", userDetails.getUsername(), 60*60));

        return "/html/Messenger";
    }

    @PostMapping("/chats/create/redirect")
    public String redirectToCreateChatWindow(@ModelAttribute("chat") Chat chat){
        if(chat.getId() == 2){
            return "redirect:/messenger/chats/create?type=group";
        }else if(chat.getId() == 3){
            return "redirect:/messenger/chats/create?type=channel";
        }else{
            return "redirect:/messenger/chats/create";
        }
    }

    @GetMapping("/chats/create")
    public String createChat(Model model, @RequestParam(value = "type", required = false, defaultValue = "private") String type, @CookieValue("username") String username){
        model.addAttribute("username", username);
        model.addAttribute("users", messengerUserService.findWithout(username));
        model.addAttribute("user", new MessengerUser());

        if(type.equals("group")){
            return "/html/chat/createGroupChat";
        }
        else if(type.equals("channel")){
            return "/html/chat/createChannel";
        }else{
            model.addAttribute("bots", botService.findAll());
            model.addAttribute("bot", new Bot());
            return "/html/chat/createPrivateChat";
        }
    }

    @PostMapping("/chats/create-chat-private-or-bot")
    public String createPrivateOrBot(@ModelAttribute("user") MessengerUser user, @RequestParam("username") String username){
        int id = chatService.createPrivateOrBotChat(user.getId(), username);

        return "redirect:/messenger/chats/"+id;
    }

    @GetMapping("/chats/{id}")
    public String showChat(@CookieValue("username")String username, @PathVariable("id") int chatId, Model model){
        if(userService.isBan(username, chatId)){
            return "redirect:/messenger";
        }
        messageWrapperService.messageWasBeRead(chatId, username);

        model.addAttribute("infoOfChat", convertor.convertToInfoOfChatDTO(chatId, username));

        return chatDAO.getReturnedHtmlFile(chatId);
    }

    @PostMapping("/chats/{id}/send-message")
    public String sendMessage(@RequestParam(value = "image") MultipartFile image, @RequestParam("text") String text, @RequestParam("user") int userId, @PathVariable("id") int chatId){
        messageWrapperService.send(image, chatId, userId, text);

        return "redirect:/messenger/chats/"+chatId;
    }

    @GetMapping("/chats/{id}/block-messages")
    public String showBlockMessages(@PathVariable("id") int chatId, Model model, @CookieValue("username") String username){
        List<BlockMessageDTO> blockMessageDTOList = convertor.convertToChatDTOOfBlockMessage(blockMessageService.findByChat(chatId), username);

        model.addAttribute("chatId", chatId);
        model.addAttribute("blockMessage", new BlockMessage());
        model.addAttribute("blockMessages", blockMessageDTOList);

        return "/html/message/blockMessageAdd";
    }

    @PostMapping("/chats/{id}/block-messages/add")
    public String addBlockMessage(@ModelAttribute("blockMessage") BlockMessage blockMessage, @PathVariable("id") int chatId){
        blockMessageService.add(blockMessage, chatId);

        return "redirect:/messenger/chats/"+chatId+"/block-messages";
    }

    @PostMapping("/chats/{id}/block-messages/remove-block-message")
    public String removeBlockMessage(@RequestParam("messageId") int messageId, @PathVariable("id") int chatId){
        blockMessageService.remove(messageId);

        return "redirect:/messenger/chats/"+chatId+"/block-messages";
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

    @GetMapping("/findChat")
    public String findChats(@RequestParam("findText") String findText, @CookieValue("username") String username) {
        List<ChatDTO> foundChatsByChatName = chatService.findChatsBySearchTextByChatName(findText, username);
        List<ChatDTO> foundChatsByMessage = chatService.findChatsBySearchTextInnerMessages(findText, username);
        List<FoundUserOfUsername> foundUsers = chatService.findUsersOfUsernameForFindBySearchText(findText, username);

        balancerOfFoundChats.addFoundedChats(username, foundChatsByChatName, foundChatsByMessage, foundUsers);
        return "redirect:/messenger";
    }
}