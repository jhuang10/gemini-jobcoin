package com.johnnyhuang.mixer.exception;


/**
 * Thrown when the user makes a bad mix request
 */
public class MixerRequestException extends MixerException {

    public MixerRequestException(String message) {
        super(message);
    }
}
