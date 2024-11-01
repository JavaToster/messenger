package com.example.Messenger.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewPasswordDTO {
    @Email(message = "this is not email, please enter email address")
    @NotBlank(message = "should be not empty")
    private String email;
    @NotBlank(message = "should be not empty, how will I understand you password, if it is blank")
    private String password;
}
