package com.example.Messenger.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewBlockMessageDTO {
    @NotBlank
    private String content;
}
