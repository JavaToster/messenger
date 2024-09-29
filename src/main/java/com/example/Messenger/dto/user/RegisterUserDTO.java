package com.example.Messenger.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RegisterUserDTO {
    @NotBlank(message = "firstname must be not empty")
    private String firstname;
    @NotBlank(message = "lastname must be not empty")
    private String lastname;
    @NotBlank(message = "username must be not empty")
    private String username;
    @NotBlank(message = "password must be not empty")
    private String password;
    @NotBlank(message = "email must be not empty")
    @Email(message = "is not email, please enter valid email address")
    private String email;
    @NotBlank(message = "phone must be not empty")
    private String phone;
    private String lang;
}
