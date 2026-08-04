package com.fooddelivery.ondc.beckn.inc;

import com.fooddelivery.ondc.dto.OndcAckResponse;
import com.fooddelivery.ondc.dto.OndcRequest;
import com.fooddelivery.ondc.util.OndcSchemaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * BPP /search_inc endpoint — receives incremental search requests for catalog deltas.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class BppSearchIncController {

    private final OndcSchemaValidator schemaValidator;

    @PostMapping("/search_inc")
    public ResponseEntity<OndcAckResponse> searchInc(@RequestBody OndcRequest request) {
        log.info("Received /search_inc from BAP: {}", request.getContext().getBapId());

        schemaValidator.validateRequest(request);

        // TODO: Trigger incremental catalog delta response

        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }
}
