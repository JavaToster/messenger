package com.example.Messenger.util;

import com.example.Messenger.dto.message.MessageWrapperDTO;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class Sorter {

    public List<MessageWrapperDTO> sortMessageWrapperDTO(List<MessageWrapperDTO> messages){
        return messages.stream().sorted(Comparator.comparingInt(MessageWrapperDTO::getId)).toList();
    }

}
