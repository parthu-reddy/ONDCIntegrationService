package com.fooddelivery.ondc.exception;

import com.fooddelivery.ondc.dto.OndcAckResponse;
import com.fooddelivery.ondc.dto.OndcError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for ONDC protocol endpoints.
 * Returns NACK responses with appropriate error codes per ONDC spec.
 */
@RestControllerAdvice(basePackages = "com.fooddelivery.ondc")
public class OndcGlobalExceptionHandler {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OndcGlobalExceptionHandler.class);

    @ExceptionHandler(OndcSignatureException.class)
    public ResponseEntity<OndcAckResponse> handleSignatureException(OndcSignatureException ex) {
        log.error("ONDC signature verification failed: {}", ex.getMessage(), ex);
        OndcError error = OndcError.builder().type("CONTEXT-ERROR").code("10001").message("Signature verification failed: " + ex.getMessage()).build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(OndcAckResponse.nack(null, error));
    }

    @ExceptionHandler(OndcRegistryException.class)
    public ResponseEntity<OndcAckResponse> handleRegistryException(OndcRegistryException ex) {
        log.error("ONDC registry error: {}", ex.getMessage(), ex);
        OndcError error = OndcError.builder().type("CORE-ERROR").code("10002").message("Registry error: " + ex.getMessage()).build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(OndcAckResponse.nack(null, error));
    }

    @ExceptionHandler(OndcCatalogException.class)
    public ResponseEntity<OndcAckResponse> handleCatalogException(OndcCatalogException ex) {
        log.error("ONDC catalog error: {}", ex.getMessage(), ex);
        OndcError error = OndcError.builder().type("DOMAIN-ERROR").code("30001").message("Catalog error: " + ex.getMessage()).build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(OndcAckResponse.nack(null, error));
    }

    @ExceptionHandler(ForbiddenFulfillmentStateException.class)
    public ResponseEntity<OndcAckResponse> handleForbiddenState(ForbiddenFulfillmentStateException ex) {
        log.error("Forbidden fulfillment state attempted: {}", ex.getMessage(), ex);
        OndcError error = OndcError.builder().type("DOMAIN-ERROR").code("50001").message(ex.getMessage()).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(OndcAckResponse.nack(null, error));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<OndcAckResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Validation error: {}", ex.getMessage(), ex);
        OndcError error = OndcError.builder().type("DOMAIN-ERROR").code("30004").message(ex.getMessage()).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(OndcAckResponse.nack(null, error));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<OndcAckResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error in ONDC service: {}", ex.getMessage(), ex);
        OndcError error = OndcError.builder().type("CORE-ERROR").code("10000").message("Internal server error").build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(OndcAckResponse.nack(null, error));
    }
}
