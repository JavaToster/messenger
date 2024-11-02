package com.example.Messenger.validators.user;

import com.example.Messenger.DAO.user.UserDAO;
import com.example.Messenger.dto.user.RegisterUserDTO;
import com.example.Messenger.exceptions.ValidateException;
import com.example.Messenger.exceptions.auth.RegistrationException;
import com.example.Messenger.util.ErrorMessageCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserDAO userDAO;
    private final ErrorMessageCreator errorMessageCreator;

    public void validate(BindingResult errors){
        if(errors.hasErrors()){
            throw new ValidateException(errorMessageCreator.createErrorMessage(errors));
        }
    }


}
