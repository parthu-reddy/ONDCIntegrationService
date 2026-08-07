package com.fooddelivery.ondc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Generic ONDC Beckn message wrapper.
 * The 'intent' or 'order' payload varies by action type.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OndcMessage {
    private Object intent;
    private Object order;
    private Object catalog;
    private Object tracking;
    private Object payment;
    private Object rating;
    @com.fasterxml.jackson.annotation.JsonProperty("order_id")
    private String orderId;
    private Object settlement;


    @java.lang.SuppressWarnings("all")
    public static class OndcMessageBuilder {
        @java.lang.SuppressWarnings("all")
        private Object intent;
        @java.lang.SuppressWarnings("all")
        private Object order;
        @java.lang.SuppressWarnings("all")
        private Object catalog;
        @java.lang.SuppressWarnings("all")
        private Object tracking;
        @java.lang.SuppressWarnings("all")
        private Object payment;
        @java.lang.SuppressWarnings("all")
        private Object rating;
        @java.lang.SuppressWarnings("all")
        private String orderId;
        @java.lang.SuppressWarnings("all")
        private Object settlement;

        @java.lang.SuppressWarnings("all")
        OndcMessageBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcMessage.OndcMessageBuilder intent(final Object intent) {
            this.intent = intent;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcMessage.OndcMessageBuilder order(final Object order) {
            this.order = order;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcMessage.OndcMessageBuilder catalog(final Object catalog) {
            this.catalog = catalog;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcMessage.OndcMessageBuilder tracking(final Object tracking) {
            this.tracking = tracking;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcMessage.OndcMessageBuilder payment(final Object payment) {
            this.payment = payment;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcMessage.OndcMessageBuilder rating(final Object rating) {
            this.rating = rating;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @com.fasterxml.jackson.annotation.JsonProperty("order_id")
        @java.lang.SuppressWarnings("all")
        public OndcMessage.OndcMessageBuilder orderId(final String orderId) {
            this.orderId = orderId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcMessage.OndcMessageBuilder settlement(final Object settlement) {
            this.settlement = settlement;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public OndcMessage build() {
            return new OndcMessage(this.intent, this.order, this.catalog, this.tracking, this.payment, this.rating, this.orderId, this.settlement);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "OndcMessage.OndcMessageBuilder(intent=" + this.intent + ", order=" + this.order + ", catalog=" + this.catalog + ", tracking=" + this.tracking + ", payment=" + this.payment + ", rating=" + this.rating + ", orderId=" + this.orderId + ", settlement=" + this.settlement + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static OndcMessage.OndcMessageBuilder builder() {
        return new OndcMessage.OndcMessageBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public Object getIntent() {
        return this.intent;
    }

    @java.lang.SuppressWarnings("all")
    public Object getOrder() {
        return this.order;
    }

    @java.lang.SuppressWarnings("all")
    public Object getCatalog() {
        return this.catalog;
    }

    @java.lang.SuppressWarnings("all")
    public Object getTracking() {
        return this.tracking;
    }

    @java.lang.SuppressWarnings("all")
    public Object getPayment() {
        return this.payment;
    }

    @java.lang.SuppressWarnings("all")
    public Object getRating() {
        return this.rating;
    }

    @java.lang.SuppressWarnings("all")
    public String getOrderId() {
        return this.orderId;
    }

    @java.lang.SuppressWarnings("all")
    public Object getSettlement() {
        return this.settlement;
    }

    @java.lang.SuppressWarnings("all")
    public void setIntent(final Object intent) {
        this.intent = intent;
    }

    @java.lang.SuppressWarnings("all")
    public void setOrder(final Object order) {
        this.order = order;
    }

    @java.lang.SuppressWarnings("all")
    public void setCatalog(final Object catalog) {
        this.catalog = catalog;
    }

    @java.lang.SuppressWarnings("all")
    public void setTracking(final Object tracking) {
        this.tracking = tracking;
    }

    @java.lang.SuppressWarnings("all")
    public void setPayment(final Object payment) {
        this.payment = payment;
    }

    @java.lang.SuppressWarnings("all")
    public void setRating(final Object rating) {
        this.rating = rating;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("order_id")
    @java.lang.SuppressWarnings("all")
    public void setOrderId(final String orderId) {
        this.orderId = orderId;
    }

    @java.lang.SuppressWarnings("all")
    public void setSettlement(final Object settlement) {
        this.settlement = settlement;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof OndcMessage)) return false;
        final OndcMessage other = (OndcMessage) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$intent = this.getIntent();
        final java.lang.Object other$intent = other.getIntent();
        if (this$intent == null ? other$intent != null : !this$intent.equals(other$intent)) return false;
        final java.lang.Object this$order = this.getOrder();
        final java.lang.Object other$order = other.getOrder();
        if (this$order == null ? other$order != null : !this$order.equals(other$order)) return false;
        final java.lang.Object this$catalog = this.getCatalog();
        final java.lang.Object other$catalog = other.getCatalog();
        if (this$catalog == null ? other$catalog != null : !this$catalog.equals(other$catalog)) return false;
        final java.lang.Object this$tracking = this.getTracking();
        final java.lang.Object other$tracking = other.getTracking();
        if (this$tracking == null ? other$tracking != null : !this$tracking.equals(other$tracking)) return false;
        final java.lang.Object this$payment = this.getPayment();
        final java.lang.Object other$payment = other.getPayment();
        if (this$payment == null ? other$payment != null : !this$payment.equals(other$payment)) return false;
        final java.lang.Object this$rating = this.getRating();
        final java.lang.Object other$rating = other.getRating();
        if (this$rating == null ? other$rating != null : !this$rating.equals(other$rating)) return false;
        final java.lang.Object this$orderId = this.getOrderId();
        final java.lang.Object other$orderId = other.getOrderId();
        if (this$orderId == null ? other$orderId != null : !this$orderId.equals(other$orderId)) return false;
        final java.lang.Object this$settlement = this.getSettlement();
        final java.lang.Object other$settlement = other.getSettlement();
        if (this$settlement == null ? other$settlement != null : !this$settlement.equals(other$settlement)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof OndcMessage;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $intent = this.getIntent();
        result = result * PRIME + ($intent == null ? 43 : $intent.hashCode());
        final java.lang.Object $order = this.getOrder();
        result = result * PRIME + ($order == null ? 43 : $order.hashCode());
        final java.lang.Object $catalog = this.getCatalog();
        result = result * PRIME + ($catalog == null ? 43 : $catalog.hashCode());
        final java.lang.Object $tracking = this.getTracking();
        result = result * PRIME + ($tracking == null ? 43 : $tracking.hashCode());
        final java.lang.Object $payment = this.getPayment();
        result = result * PRIME + ($payment == null ? 43 : $payment.hashCode());
        final java.lang.Object $rating = this.getRating();
        result = result * PRIME + ($rating == null ? 43 : $rating.hashCode());
        final java.lang.Object $orderId = this.getOrderId();
        result = result * PRIME + ($orderId == null ? 43 : $orderId.hashCode());
        final java.lang.Object $settlement = this.getSettlement();
        result = result * PRIME + ($settlement == null ? 43 : $settlement.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "OndcMessage(intent=" + this.getIntent() + ", order=" + this.getOrder() + ", catalog=" + this.getCatalog() + ", tracking=" + this.getTracking() + ", payment=" + this.getPayment() + ", rating=" + this.getRating() + ", orderId=" + this.getOrderId() + ", settlement=" + this.getSettlement() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public OndcMessage() {
    }

    @java.lang.SuppressWarnings("all")
    public OndcMessage(final Object intent, final Object order, final Object catalog, final Object tracking, final Object payment, final Object rating, final String orderId, final Object settlement) {
        this.intent = intent;
        this.order = order;
        this.catalog = catalog;
        this.tracking = tracking;
        this.payment = payment;
        this.rating = rating;
        this.orderId = orderId;
        this.settlement = settlement;
    }
}
