package com.fooddelivery.ondc.registry;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Serves the ONDC site verification HTML file.
 */
@RestController
@lombok.extern.slf4j.Slf4j
public class SiteVerificationController {
    @java.lang.SuppressWarnings("all")

    private final SiteVerificationService siteVerificationService;

    @GetMapping(value = "/ondc-site-verification.html", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getSiteVerificationHtml() {
        return ResponseEntity.ok(siteVerificationService.generateVerificationHtml());
    }

    @java.lang.SuppressWarnings("all")
    public SiteVerificationController(final SiteVerificationService siteVerificationService) {
        this.siteVerificationService = siteVerificationService;
    }
}
