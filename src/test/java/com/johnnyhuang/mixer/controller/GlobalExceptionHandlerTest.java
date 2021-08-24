package com.johnnyhuang.mixer.controller;

import com.johnnyhuang.mixer.exception.MixerRequestException;
import com.johnnyhuang.mixer.exception.MixerTransferException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleMixerRequestException() {
        ResponseEntity<Object> responseEntity = globalExceptionHandler.handleException(
                new MixerRequestException("Mix Request Error"));

        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getBody());
        assertEquals("Mix Request Error", responseEntity.getBody());

    }

    @Test
    void shouldHandleMixerTransferException() {
        ResponseEntity<Object> responseEntity = globalExceptionHandler.handleException(
                new MixerTransferException("Mix Transfer Error"));

        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getBody());
        assertEquals("Mix Transfer Error", responseEntity.getBody());

    }
}