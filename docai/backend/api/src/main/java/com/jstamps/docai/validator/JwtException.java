package com.jstamps.docai.validator;

import lombok.Getter;

public class JwtException extends MyException {
    @Getter
    private String message;

    @Getter
    private String caller;

    public JwtException(final String message, final String caller){
        super(message);
        this.message = message;
        this.caller = caller;
    }
}
