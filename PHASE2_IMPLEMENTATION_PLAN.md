# ONDC Phase 2 Implementation Plan

This document details the exact changes needed to bridge the remaining `TODO` gaps for the ONDC Integration Service. The work is split across three primary functional areas: Catalog Syndication, Beckn Async Callbacks, and RSF 2.0 Settlement.

## 1. Catalog Syndication & Mapping

### `CatalogSyndicationService.java`
- Implement `buildCatalogForSearch` to orchestrate:
  1. Calling `RestaurantServiceClient.searchRestaurants(city, gps)`
  2. Mapping via `OndcCatalogMapper`
  3. Validating the payloads with `CatalogValidationService`

### `TaxonomyMappingService.java`
- Implement a static map or configuration-based mapping from internal categories (e.g., "North Indian", "Chinese", "Fast Food") to ONDC:RET11 standard taxonomy classifications.

### `OperatingHoursMapper.java`
- Parse internal operating hours (e.g., `09:00-22:00`) to ONDC ISO 8601 `time.range` format.

## 2. Async BPP Callbacks (Beckn Protocol Compliance)

ONDC requires all BPP endpoints (`/select`, `/init`, `/confirm`, etc.) to respond with an ACK instantly and process the actual payload asynchronously, followed by an `/on_<action>` callback.

### Introduce `BppCallbackService.java`
- **[NEW]** `com.fooddelivery.ondc.beckn.bpp.BppCallbackService.java`
- Will use `@Async` and `@TransactionalEventListener` to ensure that callbacks are only sent to the BAP after the initial transaction has fully committed in our database.
- Will handle HTTP POST to the BAP's `bap_uri` for `/on_select`, `/on_init`, `/on_confirm`, `/on_status`, etc.

### Modify BPP Controllers
- **[MODIFY]** `BppSelectController.java` — Dispatch async inventory check and tax calculation, then call `on_select`.
- **[MODIFY]** `BppInitController.java` — Dispatch async quote locking, then call `on_init`.
- **[MODIFY]** `BppConfirmController.java` — Dispatch async order creation via Kafka to fulfillment, then call `on_confirm`.
- **[MODIFY]** `BppCancelController.java` — Dispatch cancellation check to ledger/restaurant, then call `on_cancel`.

## 3. Settlement & Reconciliation (RSF 2.0)

### `ReconciliationService.java`
- Implement handling for `/recon` requests from the Settlement Agency (e.g., NPNC).
- Reconcile incoming payment chunks against our `LedgerService` via Feign.
- Compute discrepancies (e.g., missing TDS, gateway fee mismatch) and prepare `/on_recon` payloads.

### `SettlementMapper.java`
- Map ONDC `ReconOrder` schema to our internal `OndcSettlementRecord` entities and `LedgerEntry` DTOs.
