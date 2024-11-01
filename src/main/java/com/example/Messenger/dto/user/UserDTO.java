package com.example.Messenger.dto.user;

import com.example.Messenger.models.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class UserDTO implements Serializable {
    private int id;
    private String username;
    private String lastTime;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    @JsonIgnore
    private List<String> imagesUrl;
    private String iconUrl;
}
