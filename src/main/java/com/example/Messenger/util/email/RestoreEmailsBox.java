package com.example.Messenger.util.email;

import com.example.Messenger.util.enums.StatusOfEqualsCodes;

public class RestoreEmailsBox {
    private int code;
    // сколько раз человек попытался ввести код, максимальное количество - 5
    private int attempt;

    public RestoreEmailsBox(int code){
        this.code = code;
    }

    public StatusOfEqualsCodes equalCodes(int code){
        if(this.code == code){
            return StatusOfEqualsCodes.EQUAL;
        }else{
            if(this.attempt > 4){
                this.attempt = 0;
                return StatusOfEqualsCodes.ATTEMPT_MAX;
            }
            this.attempt += 1;
            return StatusOfEqualsCodes.NOT_EQUAL;
        }
    }

    public int getCode() {
        return code;
    }
}
