package com.kbtg.bootcamp.posttest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;


@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(value = {InternalServerException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleInternalServerException(InternalServerException internalServerException, WebRequest request) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                internalServerException.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
