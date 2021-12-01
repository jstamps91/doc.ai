package com.jstamps.docai.validator;

import lombok.Getter;

public class AuthException extends MyException{
    @Getter
    private String message;

    public AuthException(final String message){
        super(message);
        this.message = message;
    }
}
