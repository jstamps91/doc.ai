package com.jstamps.docai.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private String messageStatus;
    private HttpStatus status;

}

