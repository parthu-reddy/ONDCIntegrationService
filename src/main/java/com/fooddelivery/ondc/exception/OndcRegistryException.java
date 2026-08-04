package com.fooddelivery.ondc.exception;

public class OndcRegistryException extends RuntimeException {
    public OndcRegistryException(String message) {
        super(message);
    }
    public OndcRegistryException(String message, Throwable cause) {
        super(message, cause);
    }
}
