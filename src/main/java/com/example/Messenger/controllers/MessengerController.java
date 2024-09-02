package com.example.Messenger.controllers;

import com.example.Messenger.dto.ExceptionMessageDTO;
import com.example.Messenger.dto.MainWindowInfoDTO;

import com.example.Messenger.dto.chat.CreatedChatDTO;
import com.example.Messenger.dto.chat.InfoOfChatDTO;
import com.example.Messenger.dto.chat.NewChatDTO;
import com.example.Messenger.dto.chat.SearchedChatsAndUsersDTO;
import com.example.Messenger.dto.message.BlockMessageDTO;
import com.example.Messenger.dto.message.ContainerOfMessagesDTO;
import com.example.Messenger.dto.message.NewBlockMessageDTO;
import com.example.Messenger.dto.user.UserDTO;
import com.example.Messenger.dto.util.SearchDTO;
import com.example.Messenger.exceptions.chat.ChatNotFoundException;
import com.example.Messenger.exceptions.message.BlockMessageValidateException;
import com.example.Messenger.exceptions.message.ImageReadingException;
import com.example.Messenger.exceptions.message.MessageBlockedException;
import com.example.Messenger.exceptions.message.MessageSendingException;
import com.example.Messenger.exceptions.user.ChatMemberNotFoundException;
import com.example.Messenger.exceptions.user.UserNotFoundException;
import com.example.Messenger.models.chat.*;
import com.example.Messenger.models.message.ContainerOfMessages;
import com.example.Messenger.models.user.User;
import com.example.Messenger.services.auth.AuthenticationService;
import com.example.Messenger.services.database.chat.ChatService;
import com.example.Messenger.services.database.message.BlockMessageService;
import com.example.Messenger.services.database.message.MessageWrapperService;
import com.example.Messenger.services.database.user.MessengerUserService;
import com.example.Messenger.services.database.user.UserService;
import com.example.Messenger.util.Convertor;
import com.example.Messenger.exceptions.security.ForbiddenException;
import com.example.Messenger.util.Finder;
import com.example.Messenger.util.threads.CheckComplaintsOfUserThread;
import com.example.Messenger.util.threads.DeleteEmptyChatsThread;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
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
    public ResponseEntity<MainWindowInfoDTO> messengerWindow(Principal principal){

        authenticationService.setUserSpecification(principal.getName());

        return new ResponseEntity<>(convertor.convertToMainInfoDTO(principal.getName()), HttpStatus.OK);
    }

    @PostMapping("/chats/create-chat-private-or-bot")
    public ResponseEntity<CreatedChatDTO> createPrivateOrBot(@RequestBody NewChatDTO newChatDTO){
        int id = chatService.createPrivateOrBotChat(newChatDTO);

        return new ResponseEntity<>(new CreatedChatDTO(id), HttpStatus.OK);
    }

    @GetMapping("/chats/{id}")
    public ResponseEntity<InfoOfChatDTO> showChat(Principal principal, @PathVariable("id") int chatId,
                                                  @RequestParam(value = "container_id", required = false) Long containerId){
        if(userService.isBan(principal.getName(), chatId)){
            throw new ForbiddenException("User is banned");
        }
        messageWrapperService.messageWasBeRead(chatId, principal.getName());

        return new ResponseEntity<>(convertor.convertToInfoOfChatDTO(chatId, principal.getName(), containerId), HttpStatus.OK);
    }
    //UPDATEME изменить параметры запроса на json
    @PostMapping("/chats/{id}/send-message")
    public ResponseEntity<ContainerOfMessagesDTO> sendMessage(@RequestParam("image") MultipartFile image,
                                                              @RequestParam("text") String text, @RequestParam("user") int userId,
                                                              @PathVariable("id") int chatId){
        ContainerOfMessages container = messageWrapperService.send(image, chatId, userId, text);

        return new ResponseEntity<>(convertor.convertToContainerOfMessagesDTO(container), HttpStatus.OK);
    }

    @GetMapping("/chats/{id}/block-messages")
    public ResponseEntity<List<BlockMessageDTO>> showBlockMessages(@PathVariable("id") int chatId,
                                                                   Principal principal){
        List<BlockMessageDTO> blockMessageDTOList = convertor.convertToChatDTOOfBlockMessage(blockMessageService.findByChat(chatId), principal.getName());

        return new ResponseEntity<>(blockMessageDTOList, HttpStatus.OK);
    }

    @PostMapping("/chats/{id}/block-messages/add")
    public ResponseEntity<HttpStatus> addBlockMessage(@RequestBody @Valid NewBlockMessageDTO blockMessage, BindingResult bindingResult,
                                                      @PathVariable("id") int chatId){
        blockMessageService.add(blockMessage, chatId, bindingResult);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/chats/{id}/block-messages/remove-block-message")
    public ResponseEntity<HttpStatus> removeBlockMessage(@RequestBody BlockMessageDTO blockMessage){
        blockMessageService.remove(blockMessage.getId());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/chats/{id}/show/block-user")
    public ResponseEntity<HttpStatus> blockUser(@RequestBody UserDTO user, @PathVariable("id") int chatId,
                                                Principal principal){
        userService.block(user.getId(), chatId, principal.getName());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/chats/{id}/show/unblock-user")
    public ResponseEntity<HttpStatus> unBlockUser(@RequestBody UserDTO user, @PathVariable("id") int chatId,
                                                  Principal principal){
        userService.unblock(user.getId(), chatId, principal.getName());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/chats/{id}/show/set-admin")
    public ResponseEntity<HttpStatus> setAdmin(@RequestBody UserDTO user, @PathVariable("id") int chatId,
                                               Principal principal){
        userService.setAdmin(user.getId(), chatId, principal.getName());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/chats/{id}/show/reset-admin")
    public ResponseEntity<HttpStatus> resetAdmin(@RequestBody UserDTO user, @PathVariable("id") int chatId,
                                                 Principal principal){
        userService.resetAdmin(user.getId(), chatId, principal.getName());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/findChat")
    public ResponseEntity<SearchedChatsAndUsersDTO> findChatsAndUsers(@RequestBody SearchDTO searchText, Principal principal) {
        List<Chat> foundChatsByChatName = finder.findChatsByTitleLikeText(searchText.getSearchText(), principal.getName());
        List<Chat> foundChatsByMessage = finder.findChatsByMessagesTextLikeText(searchText.getSearchText(), principal.getName());
        List<User> foundUsers = finder.findUsersByUsernameLikeText(searchText.getSearchText());

        return new ResponseEntity<>(convertor.convertToSearchedChatsAndUsersDTO(foundChatsByChatName, foundChatsByMessage, foundUsers, principal.getName()), HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionMessageDTO> exceptionHandle(ForbiddenException e){
        return new ResponseEntity<>(new ExceptionMessageDTO(e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionMessageDTO> exceptionHandle(UserNotFoundException e){
        return new ResponseEntity<>(new ExceptionMessageDTO(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionMessageDTO> exceptionHandle(ChatNotFoundException e){
        return new ResponseEntity<>(new ExceptionMessageDTO(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionMessageDTO> exceptionHandle(MessageBlockedException e){
        return new ResponseEntity<>(new ExceptionMessageDTO(e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionMessageDTO> exceptionHandle(MessageSendingException e){
        return new ResponseEntity<>(new ExceptionMessageDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionMessageDTO> exceptionHandle(ImageReadingException e){
        return new ResponseEntity<>(new ExceptionMessageDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionMessageDTO> exceptionHandle(IOException e){
        return new ResponseEntity<>(new ExceptionMessageDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionMessageDTO> exceptionHandle(ChatMemberNotFoundException e){
        return new ResponseEntity<>(new ExceptionMessageDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionMessageDTO> exceptionHandle(BlockMessageValidateException e){
        return new ResponseEntity<>(new ExceptionMessageDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}