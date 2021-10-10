package com.shmigel.promotionproject.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class GenericRestException extends RuntimeException {

    private HttpStatus httpStatus;

    public GenericRestException(HttpStatus httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public GenericRestException(HttpStatus httpStatus, String message) {
        this(httpStatus, message, null);
    }
}
