package com.fooddelivery.ondc.registry;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

/**
 * Serves the ONDC site verification HTML file.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class SiteVerificationController {

    private final SiteVerificationService siteVerificationService;

    @GetMapping(value = "/ondc-site-verification.html", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getSiteVerificationHtml() {
        return ResponseEntity.ok(siteVerificationService.generateVerificationHtml());
    }
}
