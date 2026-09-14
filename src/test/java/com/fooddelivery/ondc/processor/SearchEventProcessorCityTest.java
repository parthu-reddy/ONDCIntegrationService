package com.fooddelivery.ondc.processor;

import com.fooddelivery.common.repository.IIdempotencyKeyRepository;
import com.fooddelivery.ondc.beckn.bap.BapSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

/**
 * Pins where `city` comes from on an ONDC /search event.
 *
 * BppSearchController publishes a serialized OndcRequest -- the Beckn envelope {context, message}.
 * This processor previously read `city` from the ROOT of the raw map, which is always null there,
 * so the guard rejected every search and no catalogue was ever returned to the buyer app. It is now
 * read from `context.city`.
 *
 * The payload below is a real Beckn envelope. The intent paths mirror what this codebase's own
 * BapSearchService.search() builds when we act as the BAP:
 *   intent.item.descriptor.name          -> searchKey
 *   intent.fulfillment.end.location.gps  -> gps
 */
class SearchEventProcessorCityTest {

    private BapSearchService bapSearchService;
    private SearchEventProcessor processor;

    @BeforeEach
    void setUp() {
        bapSearchService = Mockito.mock(BapSearchService.class);
        IIdempotencyKeyRepository idempotencyKeyRepository = Mockito.mock(IIdempotencyKeyRepository.class);
        processor = new SearchEventProcessor(bapSearchService, idempotencyKeyRepository);
    }

    /** The Beckn envelope the producer really emits: everything nested, nothing at the root. */
    private String becknSearchEnvelope() {
        return """
                {"context":{"domain":"ONDC:RET11","action":"search","city":"std:080",
                            "core_version":"1.2.0","bap_id":"buyer-app.example.com",
                            "transaction_id":"2a96c231-e034-5303-8d4a-dab305dbba8b"},
                 "message":{"intent":{
                     "item":{"descriptor":{"name":"pizza"}},
                     "fulfillment":{"type":"Delivery",
                                    "end":{"location":{"gps":"12.971598,77.594562"}}}}}}
                """;
    }

    @Test
    void dispatchesSearchWithAllThreeValuesResolvedFromTheBecknEnvelope() {
        processor.handleSearchRequest(becknSearchEnvelope(), Map.of());

        // None of these three exist at the root. If any were still read from there it would be
        // null, the guard would reject the request, and search would never be invoked.
        Mockito.verify(bapSearchService).search(
                eq("std:080"),                    // context.city
                eq("12.971598,77.594562"),        // message.intent.fulfillment.end.location.gps
                eq("pizza"));                     // message.intent.item.descriptor.name
    }

    @Test
    void doesNotDispatchWhenTheContextIsAbsent() {
        processor.handleSearchRequest("{\"message\":{\"intent\":{}}}", Map.of());

        Mockito.verify(bapSearchService, Mockito.never()).search(any(), any(), any());
    }
}
