package contracts.messaging

/*
 * Real wire payload for ondc-order-created, from BppConfirmController:
 *   kafkaTemplate.send(TOPIC, context.getTransactionId(), objectMapper.writeValueAsString(request))
 * where request is an OndcRequest -- the Beckn envelope {context, message}, keyed by transaction id.
 *
 * OndcContext maps its fields to snake_case via @JsonProperty, so the wire keys are
 * transaction_id / message_id / bap_id / bpp_uri, NOT camelCase.
 *
 * Postel's law: only the context fields a consumer needs to route and correlate the callback are
 * asserted. The 'message' block is a large nested Beckn order and is deliberately not
 * pinned -- contracting it in full would break on every catalogue change without
 * protecting any consumer.
 *
 * KNOWN CONSUMER DEFECT (not encoded here on purpose): ConfirmEventProcessor reads
 * request.get("transactionId") and request.get("bppUri") at the ROOT in camelCase. Against this
 * real payload both are null, so it always logs "Invalid confirm request payload" and never calls
 * BapConfirmService.confirm. This contract describes the producer truthfully; the consumer is what
 * needs fixing.
 */
org.springframework.cloud.contract.spec.Contract.make {
    description("Should publish the Beckn confirm envelope to ondc-order-created")
    label("ondc_order_created")
    input { triggeredBy('fireOndcOrderCreated()') }
    outputMessage {
        sentTo('ondc.order.created')
        body([
            context: [
                domain: "ONDC:RET11",
                action: "confirm",
                core_version: "1.2.0",
                bap_id: "buyer-app.example.com",
                bap_uri: "https://buyer-app.example.com",
                bpp_id: "seller-app.example.com",
                bpp_uri: "https://seller-app.example.com",
                transaction_id: $(producer(regex('[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}'))),
                message_id: $(producer(regex('[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}')))
            ]
        ])
    }
}
