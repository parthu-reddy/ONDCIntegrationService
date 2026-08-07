package com.fooddelivery.ondc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ONDC standard error object returned in NACK responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OndcError {
    @JsonProperty("type")
    private String type;
    @JsonProperty("code")
    private String code;
    @JsonProperty("path")
    private String path;
    @JsonProperty("message")
    private String message;


    @java.lang.SuppressWarnings("all")
    public static class OndcErrorBuilder {
        @java.lang.SuppressWarnings("all")
        private String type;
        @java.lang.SuppressWarnings("all")
        private String code;
        @java.lang.SuppressWarnings("all")
        private String path;
        @java.lang.SuppressWarnings("all")
        private String message;

        @java.lang.SuppressWarnings("all")
        OndcErrorBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @JsonProperty("type")
        @java.lang.SuppressWarnings("all")
        public OndcError.OndcErrorBuilder type(final String type) {
            this.type = type;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @JsonProperty("code")
        @java.lang.SuppressWarnings("all")
        public OndcError.OndcErrorBuilder code(final String code) {
            this.code = code;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @JsonProperty("path")
        @java.lang.SuppressWarnings("all")
        public OndcError.OndcErrorBuilder path(final String path) {
            this.path = path;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @JsonProperty("message")
        @java.lang.SuppressWarnings("all")
        public OndcError.OndcErrorBuilder message(final String message) {
            this.message = message;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public OndcError build() {
            return new OndcError(this.type, this.code, this.path, this.message);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "OndcError.OndcErrorBuilder(type=" + this.type + ", code=" + this.code + ", path=" + this.path + ", message=" + this.message + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static OndcError.OndcErrorBuilder builder() {
        return new OndcError.OndcErrorBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public String getType() {
        return this.type;
    }

    @java.lang.SuppressWarnings("all")
    public String getCode() {
        return this.code;
    }

    @java.lang.SuppressWarnings("all")
    public String getPath() {
        return this.path;
    }

    @java.lang.SuppressWarnings("all")
    public String getMessage() {
        return this.message;
    }

    @JsonProperty("type")
    @java.lang.SuppressWarnings("all")
    public void setType(final String type) {
        this.type = type;
    }

    @JsonProperty("code")
    @java.lang.SuppressWarnings("all")
    public void setCode(final String code) {
        this.code = code;
    }

    @JsonProperty("path")
    @java.lang.SuppressWarnings("all")
    public void setPath(final String path) {
        this.path = path;
    }

    @JsonProperty("message")
    @java.lang.SuppressWarnings("all")
    public void setMessage(final String message) {
        this.message = message;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof OndcError)) return false;
        final OndcError other = (OndcError) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$type = this.getType();
        final java.lang.Object other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
        final java.lang.Object this$code = this.getCode();
        final java.lang.Object other$code = other.getCode();
        if (this$code == null ? other$code != null : !this$code.equals(other$code)) return false;
        final java.lang.Object this$path = this.getPath();
        final java.lang.Object other$path = other.getPath();
        if (this$path == null ? other$path != null : !this$path.equals(other$path)) return false;
        final java.lang.Object this$message = this.getMessage();
        final java.lang.Object other$message = other.getMessage();
        if (this$message == null ? other$message != null : !this$message.equals(other$message)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof OndcError;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $type = this.getType();
        result = result * PRIME + ($type == null ? 43 : $type.hashCode());
        final java.lang.Object $code = this.getCode();
        result = result * PRIME + ($code == null ? 43 : $code.hashCode());
        final java.lang.Object $path = this.getPath();
        result = result * PRIME + ($path == null ? 43 : $path.hashCode());
        final java.lang.Object $message = this.getMessage();
        result = result * PRIME + ($message == null ? 43 : $message.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "OndcError(type=" + this.getType() + ", code=" + this.getCode() + ", path=" + this.getPath() + ", message=" + this.getMessage() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public OndcError() {
    }

    @java.lang.SuppressWarnings("all")
    public OndcError(final String type, final String code, final String path, final String message) {
        this.type = type;
        this.code = code;
        this.path = path;
        this.message = message;
    }
}
