package com.example.Messenger.controllers;

import com.example.Messenger.dto.ExceptionMessageDTO;
import com.example.Messenger.dto.auth.AuthDTO;
import com.example.Messenger.dto.auth.ForgotPasswordDTO;
import com.example.Messenger.dto.auth.NewPasswordDTO;
import com.example.Messenger.dto.user.RegisterUserDTO;
import com.example.Messenger.exceptions.ValidateException;
import com.example.Messenger.exceptions.auth.AuthException;
import com.example.Messenger.exceptions.auth.RegistrationException;
import com.example.Messenger.models.user.User;
import com.example.Messenger.services.database.user.IconOfUserService;
import com.example.Messenger.services.database.user.UserService;
import com.example.Messenger.services.email.redis.languageOfApp.LanguageOfAppService;
import com.example.Messenger.balancers.UserStatusBalancer;
import com.example.Messenger.util.enums.StatusOfEqualsCodes;
import com.example.Messenger.util.enums.UserStatus;
import com.example.Messenger.validators.auth.AuthValidator;
import com.example.Messenger.validators.user.UserValidator;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthValidator authValidator;

    @PostMapping("/register")
    public ResponseEntity<HttpStatus> register(@RequestBody @Valid RegisterUserDTO registerUser, BindingResult errors){
        authValidator.validate(registerUser, errors);

        userService.register(registerUser, errors);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/forgot_password")
    public ResponseEntity<HttpStatus> sendCodeToEmailToRestore(@RequestBody @Valid ForgotPasswordDTO forgotPasswordData, BindingResult errors){
        authValidator.validate(forgotPasswordData, errors);
        userService.sendCodeToRestore(forgotPasswordData);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/check_restore_code")
    public ResponseEntity<Map<String, String>> checkRestoreCode(@RequestBody ForgotPasswordDTO forgotPasswordData, BindingResult errors){
        StatusOfEqualsCodes status = userService.checkRestoreCode(forgotPasswordData);
        return new ResponseEntity<>(Map.of("status", status.name()), HttpStatus.OK);
    }

    @PostMapping("/change_password")
    public ResponseEntity<HttpStatus> changePassword(@RequestBody @Valid NewPasswordDTO newPasswordDTO, BindingResult errors){
        authValidator.validate(errors);
        userService.changePasswordByEmail(newPasswordDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/sing")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid AuthDTO authDTO, BindingResult bindingResult){
        authValidator.validate(authDTO, bindingResult);
        String jwt = userService.login(authDTO);
        return new ResponseEntity<>(Map.of("jwtToken", jwt), HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionMessageDTO> exceptionHandle(ValidateException e){
        return new ResponseEntity<>(new ExceptionMessageDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionMessageDTO> exceptionHandle(RegistrationException e){
        return new ResponseEntity<>(new ExceptionMessageDTO(e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionMessageDTO> exceptionHandle(AuthException e){
        return new ResponseEntity<>(new ExceptionMessageDTO(e.getMessage()), HttpStatus.FORBIDDEN);
    }
}
