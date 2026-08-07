package com.fooddelivery.ondc.registry;

import com.fooddelivery.ondc.config.OndcProperties;
import com.fooddelivery.ondc.crypto.Ed25519KeyManager;
import org.springframework.stereotype.Service;
import java.util.UUID;

/**
 * Generates the content for the ondc-site-verification.html file.
 * ONDC requires this static HTML file at the root of the subscriber domain
 * to verify domain ownership during onboarding.
 */
@Service
public class SiteVerificationService {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SiteVerificationService.class);
    private final Ed25519KeyManager ed25519KeyManager;
    private final OndcProperties ondcProperties;
    private String cachedHtml;

    /**
     * Generates the HTML content for ondc-site-verification.html.
     *
     * @return HTML string containing the signed unique request ID in a meta tag
     */
    public String generateVerificationHtml() {
        if (cachedHtml == null) {
            String verificationId = ondcProperties.getRegistry().getVerificationId();
            if (verificationId == null || verificationId.isBlank()) {
                verificationId = UUID.randomUUID().toString();
            }
            String signedRequestId = ed25519KeyManager.sign(verificationId);
            cachedHtml = """
                <html>
                <head>
                    <meta name=\"ondc-site-verification\" content=\"%s\" />
                </head>
                <body>
                    ONDC Site Verification Page
                </body>
                </html>
                """.formatted(signedRequestId);
        }
        return cachedHtml;
    }

    /**
     * Returns the signed unique request ID for the meta tag.
     */
    public String generateSignedRequestId() {
        String verificationId = ondcProperties.getRegistry().getVerificationId();
        if (verificationId == null || verificationId.isBlank()) {
            verificationId = UUID.randomUUID().toString();
        }
        return ed25519KeyManager.sign(verificationId);
    }

    @java.lang.SuppressWarnings("all")
    public SiteVerificationService(final Ed25519KeyManager ed25519KeyManager, final OndcProperties ondcProperties) {
        this.ed25519KeyManager = ed25519KeyManager;
        this.ondcProperties = ondcProperties;
    }
}
