package com.example.Messenger.util;

import com.example.Messenger.exceptions.auth.RegistrationException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

@Component
public class ErrorMessageCreator {
    public String createErrorMessage(BindingResult errors){
        List<String> errorsAsString = new ArrayList<>();
        for (ObjectError error: errors.getAllErrors()){
            errorsAsString.add(error.getDefaultMessage());
        }
        return String.join(";", errorsAsString);
    }

    public String createErrorMessageByField(BindingResult errors, String field){
        List<String> errorsAsString = new ArrayList<>();
        for(FieldError error: errors.getFieldErrors(field)){
            errorsAsString.add(error.getDefaultMessage());
        }
        return String.join(";", errorsAsString);
    }
}
