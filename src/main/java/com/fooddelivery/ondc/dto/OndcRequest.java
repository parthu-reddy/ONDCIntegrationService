package com.fooddelivery.ondc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Standard ONDC request/response envelope.
 * Every Beckn API call contains a context and message.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OndcRequest {
    private OndcContext context;
    private OndcMessage message;
    @JsonProperty("error")
    private OndcError error;


    @java.lang.SuppressWarnings("all")
    public static class OndcRequestBuilder {
        @java.lang.SuppressWarnings("all")
        private OndcContext context;
        @java.lang.SuppressWarnings("all")
        private OndcMessage message;
        @java.lang.SuppressWarnings("all")
        private OndcError error;

        @java.lang.SuppressWarnings("all")
        OndcRequestBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcRequest.OndcRequestBuilder context(final OndcContext context) {
            this.context = context;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcRequest.OndcRequestBuilder message(final OndcMessage message) {
            this.message = message;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @JsonProperty("error")
        @java.lang.SuppressWarnings("all")
        public OndcRequest.OndcRequestBuilder error(final OndcError error) {
            this.error = error;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public OndcRequest build() {
            return new OndcRequest(this.context, this.message, this.error);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "OndcRequest.OndcRequestBuilder(context=" + this.context + ", message=" + this.message + ", error=" + this.error + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static OndcRequest.OndcRequestBuilder builder() {
        return new OndcRequest.OndcRequestBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public OndcContext getContext() {
        return this.context;
    }

    @java.lang.SuppressWarnings("all")
    public OndcMessage getMessage() {
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
    public void setMessage(final OndcMessage message) {
        this.message = message;
    }

    @JsonProperty("error")
    @java.lang.SuppressWarnings("all")
    public void setError(final OndcError error) {
        this.error = error;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof OndcRequest)) return false;
        final OndcRequest other = (OndcRequest) o;
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
        return other instanceof OndcRequest;
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
        return "OndcRequest(context=" + this.getContext() + ", message=" + this.getMessage() + ", error=" + this.getError() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public OndcRequest() {
    }

    @java.lang.SuppressWarnings("all")
    public OndcRequest(final OndcContext context, final OndcMessage message, final OndcError error) {
        this.context = context;
        this.message = message;
        this.error = error;
    }
}
