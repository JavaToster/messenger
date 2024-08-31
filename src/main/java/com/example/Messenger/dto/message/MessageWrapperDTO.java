package com.example.Messenger.dto.message;

import com.example.Messenger.dto.user.UserDTO;
import com.example.Messenger.models.message.MessageWrapper;
import com.example.Messenger.util.abstractClasses.MessageSpecification;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageWrapperDTO {
    private int id;
    private String content;
    private String type;
    private String sendingTime;
    private MessageSpecification specification;
    private UserDTO owner;
}