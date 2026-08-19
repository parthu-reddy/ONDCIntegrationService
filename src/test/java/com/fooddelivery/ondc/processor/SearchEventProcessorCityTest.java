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
 * NOTE: `gps` is still read from the root, which real payloads do not carry -- see
 * RandomDocuments/claude/11_OndcSearchAndDeadListeners. These tests therefore supply a root-level
 * gps as a stand-in so the dispatch path can be exercised at all. When the real
 * message.intent path is implemented, update these tests with it.
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

    /** The Beckn envelope the producer really emits: city nested under context, snake_case ids. */
    private String becknSearchEnvelope() {
        return """
                {"context":{"domain":"ONDC:RET11","action":"search","city":"std:080",
                            "core_version":"1.2.0","bap_id":"buyer-app.example.com",
                            "transaction_id":"2a96c231-e034-5303-8d4a-dab305dbba8b"},
                 "message":{"intent":{}},
                 "gps":"12.971598,77.594562","searchKey":"pizza"}
                """;
    }

    @Test
    void readsCityFromTheBecknContextNotTheRoot() {
        processor.handleSearchRequest(becknSearchEnvelope(), Map.of());

        // std:080 exists only at context.city. If city were still read from the root it would be
        // null, the guard would reject the request, and search would never be invoked.
        Mockito.verify(bapSearchService).search(eq("std:080"), any(), any());
    }

    @Test
    void doesNotDispatchWhenTheContextIsAbsent() {
        processor.handleSearchRequest("{\"message\":{},\"gps\":\"1,2\"}", Map.of());

        Mockito.verify(bapSearchService, Mockito.never()).search(any(), any(), any());
    }
}
