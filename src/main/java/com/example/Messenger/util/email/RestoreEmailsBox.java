package com.example.Messenger.util.email;

import com.example.Messenger.util.enums.StatusOfEqualsCodes;

public class RestoreEmailsBox {
    private String code;
    // сколько раз человек попытался ввести код, максимальное количество - 5
    private int attempt;

    public RestoreEmailsBox(String code){
        this.code = code;
    }

    public StatusOfEqualsCodes equalCodes(String code){
        if(this.code.equals(code)){
            return StatusOfEqualsCodes.EQUAL;
        }else if(this.attempt <= 4){
            this.attempt += 1;
            return StatusOfEqualsCodes.NOT_EQUAL;
        }else{
            return StatusOfEqualsCodes.ATTEMPT_MAX;
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
