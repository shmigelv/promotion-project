package com.shmigel.promotionproject.exception;

import org.springframework.http.HttpStatus;

public class IlligalUserInputException extends GenericRestException {
    public IlligalUserInputException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
