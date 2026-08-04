package com.fooddelivery.ondc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the ONDC Integration Service.
 * 
 * Beckn protocol endpoints (/search, /select, /init, etc.) are authenticated
 * via ONDC signature verification (OndcAuthorizationFilter), NOT via JWT.
 * 
 * Internal admin APIs (/api/ondc/**) are protected by the standard
 * PreAuthFilter from CommonLibrary (X-User-Id / X-User-Roles headers).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ONDC Beckn protocol endpoints — authenticated via ONDC signature, not JWT
                        .requestMatchers(
                                "/search", "/select", "/init", "/confirm",
                                "/cancel", "/status", "/track", "/update", "/rating",
                                "/on_search", "/on_select", "/on_init", "/on_confirm",
                                "/on_cancel", "/on_status", "/on_track",
                                "/search_inc", "/on_search_inc",
                                "/on_subscribe",
                                "/settle", "/on_settle", "/recon", "/on_recon",
                                "/receiver_recon", "/on_receiver_recon"
                        ).permitAll()
                        // ONDC site verification (static HTML)
                        .requestMatchers("/ondc-site-verification.html").permitAll()
                        // Actuator endpoints
                        .requestMatchers("/actuator/**").permitAll()
                        // Internal admin APIs require authentication via PreAuthFilter
                        .requestMatchers("/api/ondc/**").authenticated()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
