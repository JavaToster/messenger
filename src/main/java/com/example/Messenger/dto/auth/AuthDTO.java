package com.example.Messenger.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthDTO {
    @NotBlank(message = "login is empty!")
    private String login;
    @NotBlank(message = "password is empty!")
    private String password;
}
