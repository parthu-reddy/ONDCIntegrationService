# What must be built before ONDC works

**ONDC is parked as of 2026-08-20.** It compiles and its tests pass; correctness is explicitly not
required for now. This folder is the record of what is *missing*, so unparking starts from a list
rather than an excavation.

Nothing here is wired into the running service. Two implementations that existed but could never run
are preserved in [`reference/`](reference/) as `.txt` — deliberately not compiled, so they cannot be
mistaken for working code again.

## The five gaps

| # | Gap | Status today | Blocks |
|---|---|---|---|
| [1](#1-proactive-on_status) | Proactive `on_status` to the buyer app | listener written, **no producer**, now removed | ONDC protocol compliance |
| [2](#2-incremental-catalog-push) | Incremental catalog push (`on_search`) | same | catalogue freshness |
| [3](#3-the-delivery-api-ondc-expects) | `DeliveryServiceClient`'s three endpoints | client declared, **none of the endpoints exist** | fulfilment + tracking |
| [4](#4-settlement-ledger-entries) | `POST /api/v1/ledger/entries` | called by `SettlementService`, **endpoint missing** | settlement reconciliation |
| [5](#5-consumer-side-proof-for-ondcsearchrequest) | Consumer-side proof for `ondc.search.request` | producer contract exists, consumer side unverified | search correctness |

The recurring shape: **a complete-looking consumer or client with nothing on the other end.** Each
one reads as working. None of them runs.

---

## 1. Proactive `on_status`

ONDC requires a BPP to push `on_status` to the buyer app as fulfilment state advances. Today nothing
does.

`ProactiveStatusPublisher` implemented the listener side fully — status mapping,
`FulfillmentStateMachine.transition`, context rebuild from `OndcTransaction`, and
`BppCallbackService.sendCallbackWithRetry`, with idempotency on `eventId`. **Nothing ever published
to `ondc.order.status.changed`**, so it never executed once. Removed 2026-08-20; preserved at
[`reference/ProactiveStatusPublisher.java.txt`](reference/ProactiveStatusPublisher.java.txt).

**To build:**
1. Publish to `OndcKafkaConfig.TOPIC_ONDC_ORDER_STATUS_CHANGED` wherever order status advances
   (`CustomerApplication`'s order saga), carrying at minimum `{transactionId, status}`.
2. Restore the listener from reference (or
   `git show 4054641:src/main/java/com/fooddelivery/ondc/fulfillment/ProactiveStatusPublisher.java`).
3. **Fix before restoring:** it set `context.setBapUri(txn.getBapId())` — its own comment admits
   `bapUri` should come from the DB or the registry. Callbacks would go to a bogus URI. Persist
   `bapUri` on `OndcTransaction` first.
4. Write the producer contract at the same time, so the payload shape is pinned from day one.

`OndcFulfillmentMapper` (`fulfillment/`) is still in the codebase and currently used by nothing — it
was this publisher's only consumer, and was kept precisely for this restoration.

## 2. Incremental catalog push

Pushed incremental `on_search` catalogue updates to the gateway when the menu changed. Same story:
listener complete, no producer, never ran. Preserved at
[`reference/CatalogDeltaSyncService.java.txt`](reference/CatalogDeltaSyncService.java.txt).

**To build:**
1. Publish to `OndcKafkaConfig.TOPIC_ONDC_CATALOG_DELTA` from `CatalogService` on menu/outlet change,
   with `{providers: [...]}`.
2. Restore the listener.
3. **Fix before restoring:** its own comment notes it broadcasts to a single configured gateway
   instead of looking up active subscriptions. Fine for a pilot, wrong for production.

This is an optimisation over full `on_search`, not a protocol obligation — lower priority than #1.

## 3. The delivery API ONDC expects

`com.fooddelivery.ondc.client.DeliveryServiceClient` declares three endpoints on
`delivery-executive-application`. **All three are missing:**

```java
@PostMapping("/api/v1/delivery/assign-from-ondc")            // MISSING
Map<String, Object> assignDeliveryFromOndc(@RequestBody Map<String, Object> deliveryPayload);

@GetMapping("/api/v1/delivery/orders/{orderId}/status")      // MISSING
Map<String, Object> getDeliveryStatus(@PathVariable("orderId") Long orderId);

@GetMapping("/api/v1/delivery/orders/{orderId}/tracking")    // MISSING
Map<String, Object> getDeliveryTracking(@PathVariable("orderId") Long orderId);
```

The client is **never injected or called** anywhere — only its own fallback implements the interface.

**Fix the id type before building anything.** These declare `Long orderId`; every order id in this
system is a `UUID`. The client was written against a system that does not exist, which is why the
contract's example URL is the literal `/orders/1/status`.

The intended `/status` response is specified — and kept — as an **ignored** contract at
`DeliveryExecutiveApplication/src/test/resources/contracts/getDeliveryStatus.groovy`:

```groovy
ignored()
request  { method 'GET'; urlPath('/api/v1/delivery/orders/1/status') }
response { status 200; body([ status: "ASSIGNED" ]) }
```

`ignored()` makes Spring Cloud Contract generate a `@Disabled` test: the specification survives, the
build stays green, and nobody gets a false green from an unimplemented endpoint. **Remove
`ignored()` when the endpoint is built** — that is the switch that turns the spec back into a test.

`"ASSIGNED"` is a real `DeliveryStatus` value, so the payload is sound; only the endpoint and the id
type need work.

## 4. Settlement ledger entries

`SettlementService:53` calls `ledgerClient.createLedgerEntry(...)` against
`POST /api/v1/ledger/entries`. **That endpoint does not exist**, so every settlement marks its record
`FAILED` and throws. (The Feign fallback fails fast rather than returning a default — the right
design, and why this is a broken feature rather than a financial incident.)

It cannot be implemented as-is. The payload names **no accounts**:

```java
Map.of("transactionId", transactionId,        // an ONDC transaction id, a String
       "direction", "COLLECT".equals(settlementType) ? "CREDIT" : "DEBIT",
       "amount", amount,
       "currency", currency,
       "type", "ONDC_SETTLEMENT")
```

A double entry needs **two** accounts. There is no `fromId`, no `toId`, no owner type, and
`"ONDC_SETTLEMENT"` is not a `ChargeCategory`. Worse, `AccountType` has no ONDC counterparty value —
`CUSTOMER, PLATFORM, RESTAURANT, DRIVER, ADVERTISER_WALLET, GOVERNMENT` — which is itself a sign the
design was never finished.

**Decide first:** for each settlement type, which two ledger accounts move? Then extend `AccountType`
if a counterparty account is needed, and add the endpoint.

Its sibling `GET /api/v1/ledger/orders/{orderId}/total` **has** been implemented (2026-08-20) and is
covered by `DoubleEntryLedgerServiceOrderTotalTest`. Note it is **gross of refunds** — see that test.

## 5. Consumer-side proof for `ondc.search.request`

The producer contract exists and passes:
`ONDCIntegrationService/src/test/resources/contracts/messaging/ondc_search_request.groovy`.

It pins what the producer controls — the dotted topic, the transaction-id key, and the snake_case
Beckn envelope. It does **not** verify that `SearchEventProcessor` reads the intent correctly,
because ONDC code never builds the intent; an external BAP posts it and `BppSearchController`
serialises it through.

**To close:** a consumer test triggering that stub and asserting `BapSearchService.search` receives
the right city, gps and searchKey. `SearchEventProcessorCityTest` already covers the city and intent
reads directly; this would prove it end to end against the contract.

Also worth doing: `OndcMessage.intent` is a bare `Object`, so every read is a stringly-typed path
(`intent.item.descriptor.name`, `intent.fulfillment.end.location.gps`). That is exactly how the
original search bug survived. A typed DTO would make those paths compile-time facts.

---

## Two traps specific to this service

**ONDC topics use DOTS**, not hyphens — `ondc.search.request`, not `ondc-search-request`. Always
reference `OndcKafkaConfig.TOPIC_*`. Two contracts once asserted hyphenated topics nothing published
to and passed anyway, because the trigger hardcoded the same wrong literal.

**`SearchEventProcessor` swallows its exceptions.** It carries
`@RetryableTopic(attempts = "5")` and a `@DltHandler`, but the body catches `Exception` and does not
rethrow — so the retries never happen and the DLT is unreachable. The annotations advertise
durability the code does not provide. Decide whether to rethrow or drop the annotations; do not leave
them lying.

## Related records

- `RandomDocuments/claude/DECISIONS_NEEDED.md` — decisions 5 and 11 cover the parked ONDC items
- `RandomDocuments/claude/11_OndcSearchAndDeadListeners/deferred-features.md` — the original removal record
