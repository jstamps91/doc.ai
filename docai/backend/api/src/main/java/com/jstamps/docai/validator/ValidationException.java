package com.jstamps.docai.validator;

import lombok.Getter;

public class ValidationException extends RuntimeException{
    @Getter
    private String message;

    public ValidationException(final String message){
        super(message);
        this.message = message;
    }
}
