package com.example.Messenger.controllers;

import com.example.Messenger.DAO.chat.ChatDAO;
import com.example.Messenger.dto.ExceptionMessageDTO;
import com.example.Messenger.dto.MainWindowInfoDTO;

import com.example.Messenger.dto.chat.CreatedChatDTO;
import com.example.Messenger.dto.chat.InfoOfChatDTO;
import com.example.Messenger.dto.chat.SearchedChatsAndUsersDTO;
import com.example.Messenger.dto.message.BlockMessageDTO;
import com.example.Messenger.dto.message.ContainerOfMessagesDTO;
import com.example.Messenger.exceptions.chat.ChatNotFoundException;
import com.example.Messenger.exceptions.message.ImageReadingException;
import com.example.Messenger.exceptions.message.MessageBlockedException;
import com.example.Messenger.exceptions.message.MessageSendingException;
import com.example.Messenger.exceptions.user.ChatMemberNotFoundException;
import com.example.Messenger.exceptions.user.UserNotFoundException;
import com.example.Messenger.models.chat.*;
import com.example.Messenger.models.message.BlockMessage;
import com.example.Messenger.models.message.ContainerOfMessages;
import com.example.Messenger.models.user.MessengerUser;
import com.example.Messenger.models.user.User;
import com.example.Messenger.security.UserDetails;
import com.example.Messenger.services.auth.AuthenticationService;
import com.example.Messenger.services.database.chat.ChatService;
import com.example.Messenger.services.database.message.BlockMessageService;
import com.example.Messenger.services.database.message.ContainerOfMessagesService;
import com.example.Messenger.services.database.message.MessageWrapperService;
import com.example.Messenger.services.database.user.BotService;
import com.example.Messenger.services.database.user.MessengerUserService;
import com.example.Messenger.services.database.user.UserService;
import com.example.Messenger.util.Convertor;
import com.example.Messenger.balancers.BalancerOfFoundChats;
import com.example.Messenger.exceptions.security.ForbiddenException;
import com.example.Messenger.util.Finder;
import com.example.Messenger.util.threads.CheckComplaintsOfUserThread;
import com.example.Messenger.util.threads.DeleteEmptyChatsThread;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping({"/messenger", "/messenger/", "/", ""})
@RequiredArgsConstructor
public class MessengerController {
    private final UserService userService;
    private final ChatService chatService;
    private final BlockMessageService blockMessageService;
    private final DeleteEmptyChatsThread deleteEmptyChatsThread;
    private final MessengerUserService messengerUserService;
    private final MessageWrapperService messageWrapperService;
    private final CheckComplaintsOfUserThread checkComplaintsOfUserThread;
    private final AuthenticationService authenticationService;
    private final Convertor convertor;
    private final Finder finder;

    /** main window
     * главное окно*/

    @PostConstruct
    public void initialize(){
        deleteEmptyChatsThread.start();
        checkComplaintsOfUserThread.start();
    }

    @GetMapping("")
    public ResponseEntity<MainWindowInfoDTO> messengerWindow(Authentication authentication, HttpServletResponse response, Model model){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        authenticationService.setUserSpecification(userDetails.getUsername());

        response.addCookie(userService.createCookie("username", userDetails.getUsername(), 60*60));

        return new ResponseEntity<>(convertor.convertToMainInfoDTO(userDetails.getUsername()), HttpStatus.OK);
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

    @PostMapping("/chats/create-chat-private-or-bot")
    public ResponseEntity<CreatedChatDTO> createPrivateOrBot(@ModelAttribute("user") MessengerUser user, @RequestParam("username") String username){
        int id = chatService.createPrivateOrBotChat(user.getId(), username);

        return new ResponseEntity(new CreatedChatDTO(id), HttpStatus.OK);
    }

    @GetMapping("/chats/{id}")
    public ResponseEntity<InfoOfChatDTO> showChat(@CookieValue("username")String username, @PathVariable("id") int chatId, @RequestParam(value = "container_id", required = false) Long containerId){
        if(userService.isBan(username, chatId)){
            throw new ForbiddenException("User is banned");
        }
        messageWrapperService.messageWasBeRead(chatId, username);

        return new ResponseEntity(convertor.convertToInfoOfChatDTO(chatId, username, containerId), HttpStatus.OK);
    }

    @PostMapping("/chats/{id}/send-message")
    public ResponseEntity<ContainerOfMessagesDTO> sendMessage(@RequestParam(value = "image") MultipartFile image, @RequestParam("text") String text, @RequestParam("user") int userId, @PathVariable("id") int chatId){
        ContainerOfMessages container = messageWrapperService.send(image, chatId, userId, text);

        return new ResponseEntity<>(convertor.convertToContainerOfMessagesDTO(container), HttpStatus.OK);
    }

    @GetMapping("/chats/{id}/block-messages")
    public ResponseEntity<List<BlockMessageDTO>> showBlockMessages(@PathVariable("id") int chatId, Model model, @CookieValue("username") String username){
        List<BlockMessageDTO> blockMessageDTOList = convertor.convertToChatDTOOfBlockMessage(blockMessageService.findByChat(chatId), username);

        return new ResponseEntity(blockMessageDTOList, HttpStatus.OK);
    }

    @PostMapping("/chats/{id}/block-messages/add")
    public ResponseEntity<HttpStatus> addBlockMessage(@ModelAttribute("blockMessage") BlockMessage blockMessage, @PathVariable("id") int chatId){
        blockMessageService.add(blockMessage, chatId);

        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/chats/{id}/block-messages/remove-block-message")
    public ResponseEntity<HttpStatus> removeBlockMessage(@RequestParam("messageId") int messageId, @PathVariable("id") int chatId){
        blockMessageService.remove(messageId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/chats/{id}/show/block-user")
    public ResponseEntity<HttpStatus> blockUser(@RequestParam("user_id") int id, @PathVariable("id") int chatId){
        userService.block(id, chatId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/chats/{id}/show/unblock-user")
    public ResponseEntity<HttpStatus> unBlockUser(@RequestParam("user_id") int id, @PathVariable("id") int chatId){
        userService.unblock(id, chatId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/chats/{id}/show/set-admin")
    public ResponseEntity<HttpStatus> setAdmin(@RequestParam("user_id") int userId, @PathVariable("id") int chatId){
        messengerUserService.setAdmin(userId, chatId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/chats/{id}/show/reset-admin")
    public ResponseEntity<HttpStatus> resetAdmin(@RequestParam("user_id") int userId, @PathVariable("id") int chatId){
        messengerUserService.resetAdmin(userId, chatId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/findChat")
    public ResponseEntity<SearchedChatsAndUsersDTO> findChatsAndUsers(@RequestParam("findText") String searchText, @CookieValue("username") String username) {
        List<Chat> foundChatsByChatName = finder.findChatsByTitleLikeText(searchText, username);
        List<Chat> foundChatsByMessage = finder.findChatsByMessagesTextLikeText(searchText, username);
        List<User> foundUsers = finder.findUsersByUsernameLikeText(searchText);

        return new ResponseEntity<>(convertor.convertToSearchedChatsAndUsersDTO(foundChatsByChatName, foundChatsByMessage, foundUsers, username), HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionMessageDTO> exceptionHandle(ForbiddenException e){
        return new ResponseEntity(new ExceptionMessageDTO(e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionMessageDTO> exceptionHandle(UserNotFoundException e){
        return new ResponseEntity(new ExceptionMessageDTO(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionMessageDTO> exceptionHandle(ChatNotFoundException e){
        return new ResponseEntity(new ExceptionMessageDTO(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionMessageDTO> exceptionHandle(MessageBlockedException e){
        return new ResponseEntity(new ExceptionMessageDTO(e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionMessageDTO> exceptionHandle(MessageSendingException e){
        return new ResponseEntity(new ExceptionMessageDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionMessageDTO> exceptionHandle(ImageReadingException e){
        return new ResponseEntity(new ExceptionMessageDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionMessageDTO> exceptionHandle(IOException e){
        return new ResponseEntity(new ExceptionMessageDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<ExceptionMessageDTO> exceptionHandle(ChatMemberNotFoundException e){
        return new ResponseEntity<>(new ExceptionMessageDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}