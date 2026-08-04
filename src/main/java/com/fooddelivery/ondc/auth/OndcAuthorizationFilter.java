package com.fooddelivery.ondc.auth;

import com.fooddelivery.ondc.crypto.SignatureVerificationService;
import com.fooddelivery.ondc.exception.OndcSignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Verifies ONDC Authorization and X-Gateway-Authorization headers on all
 * incoming Beckn protocol requests. Runs AFTER RawBodyCachingFilter.
 * 
 * This filter replaces JWT authentication for ONDC endpoints — the ONDC
 * cryptographic signature IS the authentication mechanism.
 */
@Component
@Order(2) // After RawBodyCachingFilter (HIGHEST_PRECEDENCE)
@Slf4j
@RequiredArgsConstructor
public class OndcAuthorizationFilter extends OncePerRequestFilter {

    private final SignatureVerificationService signatureVerificationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();

        // Skip non-Beckn endpoints and the /on_subscribe (uses its own auth)
        if (!isBecknEndpoint(uri) || "/on_subscribe".equals(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get the cached raw body
        byte[] rawBody = (byte[]) request.getAttribute(RawBodyCachingFilter.CACHED_BODY_ATTRIBUTE);
        if (rawBody == null) {
            log.error("Raw body not cached for ONDC signature verification on URI: {}", uri);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().write("{\"error\":\"Internal error: body not cached\"}");
            return;
        }

        // Check Authorization header (direct request from NP)
        String authHeader = request.getHeader("Authorization");
        // Check X-Gateway-Authorization header (request via Beckn Gateway)
        String gatewayAuthHeader = request.getHeader("X-Gateway-Authorization");

        String headerToVerify = authHeader != null ? authHeader : gatewayAuthHeader;

        if (headerToVerify == null || headerToVerify.isBlank()) {
            log.warn("Missing Authorization/X-Gateway-Authorization header on ONDC endpoint: {}", uri);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"message\":{\"ack\":{\"status\":\"NACK\"}}," +
                            "\"error\":{\"type\":\"CONTEXT-ERROR\",\"code\":\"10001\"," +
                            "\"message\":\"Missing Authorization header\"}}");
            return;
        }

        try {
            signatureVerificationService.verifySignature(headerToVerify, rawBody);
            log.debug("ONDC signature verified for endpoint: {}", uri);
            filterChain.doFilter(request, response);
        } catch (OndcSignatureException e) {
            log.error("ONDC signature verification failed for {}: {}", uri, e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"message\":{\"ack\":{\"status\":\"NACK\"}}," +
                            "\"error\":{\"type\":\"CONTEXT-ERROR\",\"code\":\"10001\"," +
                            "\"message\":\"" + e.getMessage().replace("\"", "'") + "\"}}");
        }
    }

    private boolean isBecknEndpoint(String uri) {
        return uri.matches("^/(search|select|init|confirm|cancel|status|track|update|rating|" +
                "on_search|on_select|on_init|on_confirm|on_cancel|on_status|on_track|" +
                "search_inc|on_search_inc|" +
                "settle|on_settle|recon|on_recon|receiver_recon|on_receiver_recon)$");
    }
}
