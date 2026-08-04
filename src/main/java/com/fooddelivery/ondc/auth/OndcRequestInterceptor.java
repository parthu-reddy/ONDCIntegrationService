package com.fooddelivery.ondc.auth;

import com.fooddelivery.ondc.crypto.SignatureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
public class OndcRequestInterceptor implements ClientHttpRequestInterceptor {

    private final SignatureService signatureService;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                         ClientHttpRequestExecution execution) throws IOException {
        String authHeader = signatureService.createAuthorizationHeader(body);
        request.getHeaders().set("Authorization", authHeader);
        log.debug("Added ONDC Authorization header to outgoing request: {}", request.getURI());
        return execution.execute(request, body);
    }
}
