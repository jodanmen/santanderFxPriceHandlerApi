package com.santander.fxpricehandler.exception;

public class PriceNotAvailableException extends RuntimeException {

    public PriceNotAvailableException(String message) {
        super(message);
    }

    public PriceNotAvailableException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
