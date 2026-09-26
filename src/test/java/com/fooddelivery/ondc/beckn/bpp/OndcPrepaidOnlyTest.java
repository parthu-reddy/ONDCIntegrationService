package com.fooddelivery.ondc.beckn.bpp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.ondc.dto.OndcAckResponse;
import com.fooddelivery.ondc.dto.OndcContext;
import com.fooddelivery.ondc.dto.OndcMessage;
import com.fooddelivery.ondc.dto.OndcOrder;
import com.fooddelivery.ondc.dto.OndcRequest;
import com.fooddelivery.ondc.exception.OndcGlobalExceptionHandler;
import com.fooddelivery.ondc.repository.OndcTransactionRepository;
import com.fooddelivery.ondc.util.OndcSchemaValidator;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Cash on delivery is not allowed on this platform (retired 2026-09-16; the owner's rule since
 * 2026-09-26 is that it is never allowed). Beckn carries it as payment type ON-FULFILLMENT, and pay-later
 * as POST-FULFILLMENT; the seller side accepts ON-ORDER only.
 *
 * <p>Named *PrepaidOnlyTest: readiness check 1.5 forbids ON-FULFILLMENT everywhere else in the codebase,
 * and this test has to say it to prove it is refused.
 */
class OndcPrepaidOnlyTest {

    private final OndcSchemaValidator validator = new OndcSchemaValidator();

    private static OndcRequest order(String paymentType) {
        OndcOrder.OndcPaymentInfo payment = paymentType == null ? null
                : OndcOrder.OndcPaymentInfo.builder().type(paymentType).status("PAID").build();
        return OndcRequest.builder()
                .context(OndcContext.builder().domain("ONDC:RET11").action("confirm").country("IND").city("std:080")
                        .coreVersion("1.2.0").bapId("buyer.example").bapUri("https://buyer.example/ondc")
                        .bppId("seller.example").bppUri("https://seller.example/ondc")
                        .transactionId("txn-1").messageId("msg-1").timestamp("2026-09-26T10:00:00.000Z").build())
                .message(OndcMessage.builder().order(OndcOrder.builder().payment(payment).build()).build())
                .build();
    }

    @Test
    void aPrepaidOrder_isAcceptedAtInitAndConfirm() {
        assertThatCode(() -> validator.validatePrepaid(order("ON-ORDER"), true)).doesNotThrowAnyException();
        assertThatCode(() -> validator.validatePrepaid(order("ON-ORDER"), false)).doesNotThrowAnyException();
    }

    @Test
    void payOnDeliveryAndPayLater_areRefusedAtInitAndConfirm() {
        for (String type : new String[]{"ON-FULFILLMENT", "POST-FULFILLMENT", "PRE-FULFILLMENT"}) {
            for (boolean confirm : new boolean[]{true, false}) {
                assertThatThrownBy(() -> validator.validatePrepaid(order(type), confirm))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining(type).hasMessageContaining("not accepted");
            }
        }
    }

    @Test
    void aConfirmWithoutPaymentTerms_isRefused_butAnInitMayLeaveThemToUs() {
        assertThatThrownBy(() -> validator.validatePrepaid(order(null), true))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("payment.type");
        assertThatCode(() -> validator.validatePrepaid(order(null), false)).doesNotThrowAnyException();
    }

    @Test
    void aPayOnDeliveryConfirm_isNackedBeforeAnythingIsRecordedOrPublished() {
        OndcTransactionRepository transactions = mock(OndcTransactionRepository.class);
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, String> kafka = mock(KafkaTemplate.class);
        BppCallbackService callbacks = mock(BppCallbackService.class);
        BppConfirmController controller = new BppConfirmController(validator, transactions, kafka, callbacks, new ObjectMapper());

        IllegalArgumentException refused = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> controller.confirm(order("ON-FULFILLMENT")));
        verifyNoInteractions(transactions, kafka, callbacks);

        // What the buyer app receives: a Beckn NACK with a domain error, not an order.
        ResponseEntity<OndcAckResponse> nack = new OndcGlobalExceptionHandler().handleIllegalArgument(refused);
        assertThat(nack.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(nack.getBody().getError().getType()).isEqualTo("DOMAIN-ERROR");
    }
}
