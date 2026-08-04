package com.fooddelivery.ondc.exception;

public class OndcCatalogException extends RuntimeException {
    public OndcCatalogException(String message) {
        super(message);
    }
    public OndcCatalogException(String message, Throwable cause) {
        super(message, cause);
    }
}
