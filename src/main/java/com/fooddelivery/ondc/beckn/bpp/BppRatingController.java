package com.fooddelivery.ondc.beckn.bpp;

import org.springframework.transaction.annotation.Transactional;

import com.fooddelivery.ondc.dto.OndcAckResponse;
import com.fooddelivery.ondc.dto.OndcRequest;
import com.fooddelivery.ondc.entity.OndcTransaction;
import com.fooddelivery.ondc.repository.OndcTransactionRepository;
import com.fooddelivery.ondc.util.OndcSchemaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * BPP /rating endpoint — receives ratings from BAP for fulfilled orders.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BppRatingController {

    private final OndcSchemaValidator schemaValidator;
    private final OndcTransactionRepository transactionRepository;

    @PostMapping("/rating")
    public ResponseEntity<OndcAckResponse> rating(@RequestBody OndcRequest request) {
        log.info("Received /rating from BAP: {}, transaction_id: {}",
                request.getContext().getBapId(), request.getContext().getTransactionId());

        schemaValidator.validateRequest(request);

        OndcTransaction txn = OndcTransaction.builder()
                .transactionId(request.getContext().getTransactionId())
                .messageId(request.getContext().getMessageId())
                .action("rating")
                .bapId(request.getContext().getBapId())
                .bppId(request.getContext().getBppId())
                .state("RECEIVED")
                .build();
        transactionRepository.save(txn);

        return ResponseEntity.ok(OndcAckResponse.ack(request.getContext()));
    }
}
