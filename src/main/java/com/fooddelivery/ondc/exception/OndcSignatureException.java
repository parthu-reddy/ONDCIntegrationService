package com.fooddelivery.ondc.exception;

public class OndcSignatureException extends RuntimeException {
    public OndcSignatureException(String message) {
        super(message);
    }
    public OndcSignatureException(String message, Throwable cause) {
        super(message, cause);
    }
}
