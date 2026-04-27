package com.hostmint.app.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.hostmint.app.domain.enumeration.LogLevel;
import com.hostmint.app.service.AuditLogService;
import com.hostmint.app.service.dto.AuditLogDTO;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class InternalAuditServiceImplTest {

    @Mock
    private AuditLogService auditLogService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldPersistAuditLogWithStructuredContextAndTruncation() {
        InternalAuditServiceImpl service = new InternalAuditServiceImpl(auditLogService);
        UUID entityId = UUID.randomUUID();

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("auditor", "n/a", Collections.emptyList())
        );

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.10");
        request.addHeader("User-Agent", "x".repeat(700));
        request.setAttribute("correlationId", "corr-123");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        service.log("ACTION", "Entity", entityId, LogLevel.INFO, "m".repeat(1200), null, "{\"key\":\"value\"}");

        ArgumentCaptor<AuditLogDTO> captor = ArgumentCaptor.forClass(AuditLogDTO.class);
        verify(auditLogService).save(captor.capture());
        AuditLogDTO saved = captor.getValue();

        assertThat(saved.getAction()).isEqualTo("ACTION");
        assertThat(saved.getEntityName()).isEqualTo("Entity");
        assertThat(saved.getEntityId()).isEqualTo(entityId);
        assertThat(saved.getLevel()).isEqualTo(LogLevel.INFO);
        assertThat(saved.getPrincipal()).isEqualTo("auditor");
        assertThat(saved.getIpAddress()).isEqualTo("192.168.1.10");
        assertThat(saved.getCorrelationId()).isEqualTo("corr-123");
        assertThat(saved.getMetadata()).isEqualTo("{\"key\":\"value\"}");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getMessage()).hasSize(1000).endsWith("...");
        assertThat(saved.getUserAgent()).hasSize(500).endsWith("...");
    }

    @Test
    void shouldUseFallbackContextWhenNoHttpRequestExists() {
        InternalAuditServiceImpl service = new InternalAuditServiceImpl(auditLogService);

        service.log("ACTION", "Entity", null, LogLevel.INFO, "message", null, null);

        ArgumentCaptor<AuditLogDTO> captor = ArgumentCaptor.forClass(AuditLogDTO.class);
        verify(auditLogService).save(captor.capture());
        AuditLogDTO saved = captor.getValue();

        assertThat(saved.getPrincipal()).isEqualTo("system");
        assertThat(saved.getIpAddress()).isEqualTo("127.0.0.1");
        assertThat(saved.getCorrelationId()).isNotBlank();
    }
}
