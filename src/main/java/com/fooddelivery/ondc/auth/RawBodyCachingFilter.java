package com.fooddelivery.ondc.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import java.io.IOException;

/**
 * Caches the raw HTTP request body bytes BEFORE any other filter or JSON
 * deserializer reads the input stream. This is CRITICAL for ONDC signature
 * verification — the BLAKE-512 hash must be computed on the exact raw bytes
 * as received, not on re-serialized JSON.
 * 
 * This filter MUST be the highest-precedence filter in the chain.
 * 
 * Note: Spring's ContentCachingRequestWrapper only caches after the stream
 * is read. We use a custom approach to eagerly cache the body.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RawBodyCachingFilter extends OncePerRequestFilter {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RawBodyCachingFilter.class);
    public static final String CACHED_BODY_ATTRIBUTE = "ONDC_RAW_BODY";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Only cache for ONDC Beckn protocol endpoints (POST with JSON body)
        if ("POST".equalsIgnoreCase(request.getMethod()) && isBecknEndpoint(request.getRequestURI())) {
            byte[] rawBody = request.getInputStream().readAllBytes();
            request.setAttribute(CACHED_BODY_ATTRIBUTE, rawBody);
            // Wrap so downstream filters/controllers can still read the body
            CachedBodyRequestWrapper wrappedRequest = new CachedBodyRequestWrapper(request, rawBody);
            filterChain.doFilter(wrappedRequest, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private boolean isBecknEndpoint(String uri) {
        return uri.matches("^/(search|select|init|confirm|cancel|status|track|update|rating|" + "on_search|on_select|on_init|on_confirm|on_cancel|on_status|on_track|" + "search_inc|on_search_inc|on_subscribe|" + "settle|on_settle|recon|on_recon|receiver_recon|on_receiver_recon)$");
    }
}
