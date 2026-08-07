package com.fooddelivery.ondc.auth;

import com.fooddelivery.ondc.crypto.SignatureService;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * Intercepts outgoing HTTP requests to ONDC network participants and adds
 * the cryptographic Authorization header with Ed25519 signature.
 */
@Component
public class OndcRequestInterceptor implements ClientHttpRequestInterceptor {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OndcRequestInterceptor.class);
    private final SignatureService signatureService;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String authHeader = signatureService.createAuthorizationHeader(body);
        request.getHeaders().set("Authorization", authHeader);
        log.debug("Added ONDC Authorization header to outgoing request: {}", request.getURI());
        return execution.execute(request, body);
    }

    @java.lang.SuppressWarnings("all")
    public OndcRequestInterceptor(final SignatureService signatureService) {
        this.signatureService = signatureService;
    }
}
