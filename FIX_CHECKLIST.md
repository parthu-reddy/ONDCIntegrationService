# ONDC Integration Service — Fix Checklist

Comprehensive audit results with 23 issues found. See the full implementation plan for details.

## 🔴 P0 — Compilation Blockers
- [ ] **#1** Add `spring-boot-starter-security` to pom.xml
- [ ] **#2** Remove hardcoded `url` from all 7 Feign clients
- [ ] **#3** Fix Feign client `name` to match actual Eureka service names
- [ ] **#4** Rewrite V1 Flyway migration — UUID PKs + missing columns
- [ ] **#5** Align `OndcProperties` fields with YAML config structure
- [ ] **#6** Fix port conflict: Dockerfile 8095 → 8098

## 🟡 P1 — Missing Implementations
- [ ] **#7** Implement `OndcCatalogMapper` (brand→provider, outlet→location, item mapping)
- [ ] **#8** Implement `BapSearchService.search()` body
- [ ] **#9** Implement `ProactiveStatusPublisher.handleStatusChange()`
- [ ] **#10** Wire `SettlementService` to LedgerService + PaymentService via Feign/Kafka
- [ ] **#11** Create base `application.yml`
- [ ] **#15** Add `outbox_events` table to Flyway migration
- [ ] **#16** Create `SearchEventProcessor` Kafka consumer
- [ ] **#17** Create `ConfirmEventProcessor` Kafka consumer

## 🟠 P2 — Functional Gaps
- [ ] **#12** Wire `OndcRequestInterceptor` into RestTemplate
- [ ] **#13** Replace raw `Object` returns in Feign clients with proper DTOs
- [ ] **#14** Verify X25519 shared secret against ONDC production key
- [ ] **#18** Add `subscriberUrl` to `OndcProperties`, fix `bap_uri`
- [ ] **#19** Add `@Transactional` to BPP controllers or use Outbox pattern
- [ ] **#20** Add site verification HTML endpoint
- [ ] **#21** Create missing BAP services (Select, Init, Confirm, Cancel)

## 🔵 P3 — Deployment & Config
- [ ] **#22** Align docker-compose port mapping with actual service port
- [ ] **#23** Fix `spring.redis` → `spring.data.redis` in application-dev.yml
