package contracts.messaging

/*
 * Real wire payload for ondc-settlement-event, from SettlementService:
 *   String.format("{\"transactionId\":\"%s\",\"type\":\"%s\",\"amount\":%s,\"currency\":\"%s\"}", ...)
 * Note `amount` is interpolated unquoted, so it is a JSON NUMBER here -- unlike wallet/ledger
 * events, where amounts are strings. Keyed by transactionId.
 */
org.springframework.cloud.contract.spec.Contract.make {
    description("Should publish a settlement event to ondc-settlement-event")
    label("ondc_settlement_event")
    input {
        triggeredBy('fireOndcSettlementEvent()')
    }
    outputMessage {
        sentTo('ondc.settlement.event')
        body([
            transactionId: $(producer(regex('[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}'))),
            type: "COLLECT",
            amount: 250.00,
            currency: "INR"
        ])
    }
}
