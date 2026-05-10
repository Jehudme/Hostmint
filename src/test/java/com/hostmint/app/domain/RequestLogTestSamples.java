package com.hostmint.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RequestLogTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static RequestLog getRequestLogSample1() {
        return new RequestLog()
            .id(1L)
            .correlationId("correlationId1")
            .path("path1")
            .statusCode(1)
            .durationMs(1L)
            .principal("principal1")
            .ipAddress("ipAddress1")
            .errorCode("errorCode1")
            .errorMessage("errorMessage1");
    }

    public static RequestLog getRequestLogSample2() {
        return new RequestLog()
            .id(2L)
            .correlationId("correlationId2")
            .path("path2")
            .statusCode(2)
            .durationMs(2L)
            .principal("principal2")
            .ipAddress("ipAddress2")
            .errorCode("errorCode2")
            .errorMessage("errorMessage2");
    }

    public static RequestLog getRequestLogRandomSampleGenerator() {
        return new RequestLog()
            .id(longCount.incrementAndGet())
            .correlationId(UUID.randomUUID().toString())
            .path(UUID.randomUUID().toString())
            .statusCode(intCount.incrementAndGet())
            .durationMs(longCount.incrementAndGet())
            .principal(UUID.randomUUID().toString())
            .ipAddress(UUID.randomUUID().toString())
            .errorCode(UUID.randomUUID().toString())
            .errorMessage(UUID.randomUUID().toString());
    }
}
