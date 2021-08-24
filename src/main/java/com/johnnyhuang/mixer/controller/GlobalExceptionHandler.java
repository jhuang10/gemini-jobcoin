package com.johnnyhuang.mixer.controller;

import com.johnnyhuang.mixer.exception.MixerRequestException;
import com.johnnyhuang.mixer.exception.MixerTransferException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global Exception Handler
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MixerRequestException.class)
    public ResponseEntity<Object> handleException(MixerRequestException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler(MixerTransferException.class)
    public ResponseEntity<Object> handleException(MixerTransferException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }
}
