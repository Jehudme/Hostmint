package com.hostmint.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AuditLogTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static AuditLog getAuditLogSample1() {
        return new AuditLog()
            .id(1L)
            .action("action1")
            .entityName("entityName1")
            .entityId(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .message("message1")
            .principal("principal1")
            .correlationId("correlationId1")
            .ipAddress("ipAddress1")
            .userAgent("userAgent1");
    }

    public static AuditLog getAuditLogSample2() {
        return new AuditLog()
            .id(2L)
            .action("action2")
            .entityName("entityName2")
            .entityId(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .message("message2")
            .principal("principal2")
            .correlationId("correlationId2")
            .ipAddress("ipAddress2")
            .userAgent("userAgent2");
    }

    public static AuditLog getAuditLogRandomSampleGenerator() {
        return new AuditLog()
            .id(longCount.incrementAndGet())
            .action(UUID.randomUUID().toString())
            .entityName(UUID.randomUUID().toString())
            .entityId(UUID.randomUUID())
            .message(UUID.randomUUID().toString())
            .principal(UUID.randomUUID().toString())
            .correlationId(UUID.randomUUID().toString())
            .ipAddress(UUID.randomUUID().toString())
            .userAgent(UUID.randomUUID().toString());
    }
}
