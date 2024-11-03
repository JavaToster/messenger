package com.example.Messenger.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordDTO {
    @Email(message = "not valid data, please enter valid email address")
    @NotBlank(message = "field of email should be not empty")
    private String email;
    private String code;
}
