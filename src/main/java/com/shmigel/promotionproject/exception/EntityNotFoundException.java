package com.shmigel.promotionproject.exception;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends GenericRestException {

    public EntityNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

}
