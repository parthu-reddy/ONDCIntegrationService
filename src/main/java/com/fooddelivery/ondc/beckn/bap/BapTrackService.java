package com.fooddelivery.ondc.beckn.bap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class BapTrackService {
    private final RestTemplate ondcRestTemplate;

    public void track(String bppUri, String transactionId) {
        log.info("BAP sending /track to BPP: {}, transaction_id: {}", bppUri, transactionId);
    }
}
