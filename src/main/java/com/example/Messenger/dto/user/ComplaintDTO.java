package com.example.Messenger.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ComplaintDTO {
    @Size(min = 100, max = 3000, message = "size of complaint not valid")
    @NotBlank(message = "complaint not blank")
    private String complaint;
}
