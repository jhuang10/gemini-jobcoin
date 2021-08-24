package com.johnnyhuang.mixer.exception;


/**
 * Thrown when the transfer fails to transact funds
 */
public class MixerTransferException extends MixerException {

    public MixerTransferException(String message) {
        super(message);
    }

}
