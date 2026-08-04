package com.fooddelivery.ondc.auth;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Request wrapper that allows the body to be read multiple times.
 * Used by RawBodyCachingFilter to cache raw bytes and still allow
 * downstream controllers to deserialize the JSON body.
 */
public class CachedBodyRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] cachedBody;

    public CachedBodyRequestWrapper(HttpServletRequest request, byte[] body) {
        super(request);
        this.cachedBody = body;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new CachedServletInputStream(cachedBody);
    }

    private static class CachedServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream inputStream;

        public CachedServletInputStream(byte[] body) {
            this.inputStream = new ByteArrayInputStream(body);
        }

        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            // Not needed for synchronous processing
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }
    }
}
