package com.fooddelivery.ondc.catalog;

import com.fooddelivery.ondc.exception.OndcCatalogException;
import org.springframework.stereotype.Service;

/**
 * Validates ONDC catalog payloads before broadcasting.
 * Enforces FSSAI, GSTIN format, mandatory fields.
 */
@Service
public class CatalogValidationService {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CatalogValidationService.class);

    /**
     * Validates FSSAI license number (must be exactly 14 digits).
     */
    public void validateFssai(String fssai) {
        if (fssai == null || !fssai.matches("\\d{14}")) {
            throw new OndcCatalogException("Invalid FSSAI license: \'" + fssai + "\'. Must be exactly 14 digits.");
        }
    }

    /**
     * Validates GSTIN format (15 characters: 2-digit state + 10-char PAN + 1-char entity + 1-char check + Z).
     */
    public void validateGstin(String gstin) {
        if (gstin == null || !gstin.matches("\\d{2}[A-Z]{5}\\d{4}[A-Z]\\d[Z][A-Z\\d]")) {
            throw new OndcCatalogException("Invalid GSTIN format: \'" + gstin + "\'.");
        }
    }
}
