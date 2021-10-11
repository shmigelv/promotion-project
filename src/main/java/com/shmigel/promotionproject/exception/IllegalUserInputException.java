package com.shmigel.promotionproject.exception;

import org.springframework.http.HttpStatus;

public class IllegalUserInputException extends GenericRestException {
    public IllegalUserInputException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
