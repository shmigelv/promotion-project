package com.shmigel.promotionproject.controller.filter;

import com.shmigel.promotionproject.exception.GenericRestException;
import com.shmigel.promotionproject.model.dto.ErrorDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Log4j2
@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(value = GenericRestException.class)
    public ResponseEntity<ErrorDTO> handleGenericException(GenericRestException genericException) {
        log.info("Exception thrown during request execution " + genericException);
        return ResponseEntity.status(genericException.getHttpStatus())
                .body(new ErrorDTO(genericException.getHttpStatus().value(), genericException.getMessage()));
    }

}
