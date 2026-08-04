package com.fooddelivery.ondc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Synchronous ACK/NACK response returned immediately for every ONDC action.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OndcAckResponse {

    private OndcContext context;
    private Message message;
    private OndcError error;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private Ack ack;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ack {
        private String status; // "ACK" or "NACK"
    }

    public static OndcAckResponse ack(OndcContext context) {
        return OndcAckResponse.builder()
                .context(context)
                .message(Message.builder()
                        .ack(Ack.builder().status("ACK").build())
                        .build())
                .build();
    }

    public static OndcAckResponse nack(OndcContext context, OndcError error) {
        return OndcAckResponse.builder()
                .context(context)
                .message(Message.builder()
                        .ack(Ack.builder().status("NACK").build())
                        .build())
                .error(error)
                .build();
    }
}
