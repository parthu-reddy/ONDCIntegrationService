package com.fooddelivery.ondc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Synchronous ACK/NACK response returned immediately for every ONDC action.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OndcAckResponse {
    private OndcContext context;
    private Message message;
    private OndcError error;


    public static class Message {
        private Ack ack;


        @java.lang.SuppressWarnings("all")
        public static class MessageBuilder {
            @java.lang.SuppressWarnings("all")
            private Ack ack;

            @java.lang.SuppressWarnings("all")
            MessageBuilder() {
            }

            /**
             * @return {@code this}.
             */
            @java.lang.SuppressWarnings("all")
            public OndcAckResponse.Message.MessageBuilder ack(final Ack ack) {
                this.ack = ack;
                return this;
            }

            @java.lang.SuppressWarnings("all")
            public OndcAckResponse.Message build() {
                return new OndcAckResponse.Message(this.ack);
            }

            @java.lang.Override
            @java.lang.SuppressWarnings("all")
            public java.lang.String toString() {
                return "OndcAckResponse.Message.MessageBuilder(ack=" + this.ack + ")";
            }
        }

        @java.lang.SuppressWarnings("all")
        public static OndcAckResponse.Message.MessageBuilder builder() {
            return new OndcAckResponse.Message.MessageBuilder();
        }

        @java.lang.SuppressWarnings("all")
        public Ack getAck() {
            return this.ack;
        }

        @java.lang.SuppressWarnings("all")
        public void setAck(final Ack ack) {
            this.ack = ack;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof OndcAckResponse.Message)) return false;
            final OndcAckResponse.Message other = (OndcAckResponse.Message) o;
            if (!other.canEqual((java.lang.Object) this)) return false;
            final java.lang.Object this$ack = this.getAck();
            final java.lang.Object other$ack = other.getAck();
            if (this$ack == null ? other$ack != null : !this$ack.equals(other$ack)) return false;
            return true;
        }

        @java.lang.SuppressWarnings("all")
        protected boolean canEqual(final java.lang.Object other) {
            return other instanceof OndcAckResponse.Message;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final java.lang.Object $ack = this.getAck();
            result = result * PRIME + ($ack == null ? 43 : $ack.hashCode());
            return result;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "OndcAckResponse.Message(ack=" + this.getAck() + ")";
        }

        @java.lang.SuppressWarnings("all")
        public Message() {
        }

        @java.lang.SuppressWarnings("all")
        public Message(final Ack ack) {
            this.ack = ack;
        }
    }


    public static class Ack {
        private String status; // "ACK" or "NACK"


        @java.lang.SuppressWarnings("all")
        public static class AckBuilder {
            @java.lang.SuppressWarnings("all")
            private String status;

            @java.lang.SuppressWarnings("all")
            AckBuilder() {
            }

            /**
             * @return {@code this}.
             */
            @java.lang.SuppressWarnings("all")
            public OndcAckResponse.Ack.AckBuilder status(final String status) {
                this.status = status;
                return this;
            }

            @java.lang.SuppressWarnings("all")
            public OndcAckResponse.Ack build() {
                return new OndcAckResponse.Ack(this.status);
            }

            @java.lang.Override
            @java.lang.SuppressWarnings("all")
            public java.lang.String toString() {
                return "OndcAckResponse.Ack.AckBuilder(status=" + this.status + ")";
            }
        }

        @java.lang.SuppressWarnings("all")
        public static OndcAckResponse.Ack.AckBuilder builder() {
            return new OndcAckResponse.Ack.AckBuilder();
        }

        @java.lang.SuppressWarnings("all")
        public String getStatus() {
            return this.status;
        }

        @java.lang.SuppressWarnings("all")
        public void setStatus(final String status) {
            this.status = status;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof OndcAckResponse.Ack)) return false;
            final OndcAckResponse.Ack other = (OndcAckResponse.Ack) o;
            if (!other.canEqual((java.lang.Object) this)) return false;
            final java.lang.Object this$status = this.getStatus();
            final java.lang.Object other$status = other.getStatus();
            if (this$status == null ? other$status != null : !this$status.equals(other$status)) return false;
            return true;
        }

        @java.lang.SuppressWarnings("all")
        protected boolean canEqual(final java.lang.Object other) {
            return other instanceof OndcAckResponse.Ack;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final java.lang.Object $status = this.getStatus();
            result = result * PRIME + ($status == null ? 43 : $status.hashCode());
            return result;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "OndcAckResponse.Ack(status=" + this.getStatus() + ")";
        }

        @java.lang.SuppressWarnings("all")
        public Ack() {
        }

        @java.lang.SuppressWarnings("all")
        public Ack(final String status) {
            this.status = status;
        }
    }

    public static OndcAckResponse ack(OndcContext context) {
        return OndcAckResponse.builder().context(context).message(Message.builder().ack(Ack.builder().status("ACK").build()).build()).build();
    }

    public static OndcAckResponse nack(OndcContext context, OndcError error) {
        return OndcAckResponse.builder().context(context).message(Message.builder().ack(Ack.builder().status("NACK").build()).build()).error(error).build();
    }


    @java.lang.SuppressWarnings("all")
    public static class OndcAckResponseBuilder {
        @java.lang.SuppressWarnings("all")
        private OndcContext context;
        @java.lang.SuppressWarnings("all")
        private Message message;
        @java.lang.SuppressWarnings("all")
        private OndcError error;

        @java.lang.SuppressWarnings("all")
        OndcAckResponseBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcAckResponse.OndcAckResponseBuilder context(final OndcContext context) {
            this.context = context;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcAckResponse.OndcAckResponseBuilder message(final Message message) {
            this.message = message;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcAckResponse.OndcAckResponseBuilder error(final OndcError error) {
            this.error = error;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public OndcAckResponse build() {
            return new OndcAckResponse(this.context, this.message, this.error);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "OndcAckResponse.OndcAckResponseBuilder(context=" + this.context + ", message=" + this.message + ", error=" + this.error + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static OndcAckResponse.OndcAckResponseBuilder builder() {
        return new OndcAckResponse.OndcAckResponseBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public OndcContext getContext() {
        return this.context;
    }

    @java.lang.SuppressWarnings("all")
    public Message getMessage() {
        return this.message;
    }

    @java.lang.SuppressWarnings("all")
    public OndcError getError() {
        return this.error;
    }

    @java.lang.SuppressWarnings("all")
    public void setContext(final OndcContext context) {
        this.context = context;
    }

    @java.lang.SuppressWarnings("all")
    public void setMessage(final Message message) {
        this.message = message;
    }

    @java.lang.SuppressWarnings("all")
    public void setError(final OndcError error) {
        this.error = error;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof OndcAckResponse)) return false;
        final OndcAckResponse other = (OndcAckResponse) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$context = this.getContext();
        final java.lang.Object other$context = other.getContext();
        if (this$context == null ? other$context != null : !this$context.equals(other$context)) return false;
        final java.lang.Object this$message = this.getMessage();
        final java.lang.Object other$message = other.getMessage();
        if (this$message == null ? other$message != null : !this$message.equals(other$message)) return false;
        final java.lang.Object this$error = this.getError();
        final java.lang.Object other$error = other.getError();
        if (this$error == null ? other$error != null : !this$error.equals(other$error)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof OndcAckResponse;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $context = this.getContext();
        result = result * PRIME + ($context == null ? 43 : $context.hashCode());
        final java.lang.Object $message = this.getMessage();
        result = result * PRIME + ($message == null ? 43 : $message.hashCode());
        final java.lang.Object $error = this.getError();
        result = result * PRIME + ($error == null ? 43 : $error.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "OndcAckResponse(context=" + this.getContext() + ", message=" + this.getMessage() + ", error=" + this.getError() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public OndcAckResponse() {
    }

    @java.lang.SuppressWarnings("all")
    public OndcAckResponse(final OndcContext context, final Message message, final OndcError error) {
        this.context = context;
        this.message = message;
        this.error = error;
    }
}
