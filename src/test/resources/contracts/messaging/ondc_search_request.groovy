package contracts.messaging

/*
 * ondc.search.request -- an inbound BAP /search forwarded for async catalogue lookup.
 *
 * Producer: BppSearchController.search() (the BPP /search endpoint). It ACKs synchronously, then
 * publishes the whole serialized OndcRequest keyed by the transaction id.
 * Consumer: SearchEventProcessor, which dispatches to BapSearchService.
 *
 * Two things this contract exists to hold still:
 *
 * 1. The context is SNAKE_CASE. OndcContext annotates transaction_id, bap_id, bap_uri and
 *    core_version with @JsonProperty, so they do NOT serialize as camelCase. SearchEventProcessor
 *    previously read transactionId and city at the ROOT in camelCase; both were always null, the
 *    guard rejected every request, and no catalogue was ever returned.
 *
 * 2. message.intent carries the search terms at a specific depth:
 *      intent.item.descriptor.name          -> searchKey
 *      intent.fulfillment.end.location.gps  -> gps
 *    OndcMessage types intent as a bare Object, so the compiler cannot protect these paths.
 *    The structure matches BapSearchService.search(), which builds it when we act as the BAP.
 *
 * Note ONDC topics use DOTS (ondc.search.request), not hyphens.
 * Note @JsonInclude(NON_NULL) on both DTOs -- unset fields are absent, not null.
 */
org.springframework.cloud.contract.spec.Contract.make {
    description("Should publish an inbound BAP search request to ondc.search.request as a Beckn envelope, keyed by transaction id")
    label("ondc_search_request")
    input { triggeredBy('fireOndcSearchRequest()') }
    outputMessage {
        sentTo('ondc.search.request')
        body([
            context: [
                domain: "ONDC:RET11",
                action: "search",
                country: "IND",
                city: "std:080",
                core_version: "1.2.0",
                bap_id: "buyer-app.example.com",
                bap_uri: "https://buyer-app.example.com",
                transaction_id: $(producer(regex('[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}'))),
                message_id: $(producer(regex('[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}'))),
                timestamp: $(producer(regex('[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}[.][0-9]{3}Z')))
            ],
            message: [
                intent: [
                    item: [ descriptor: [ name: "biryani" ] ],
                    fulfillment: [
                        type: "Delivery",
                        end: [ location: [ gps: $(producer(regex('-?[0-9]{1,3}[.][0-9]+,-?[0-9]{1,3}[.][0-9]+'))) ] ]
                    ]
                ]
            ]
        ])
    }
}
