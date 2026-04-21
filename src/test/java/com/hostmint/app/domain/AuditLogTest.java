package com.hostmint.app.domain;

import static com.hostmint.app.domain.AuditLogTestSamples.*;
import static com.hostmint.app.domain.ProjectTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.hostmint.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AuditLogTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuditLog.class);
        AuditLog auditLog1 = getAuditLogSample1();
        AuditLog auditLog2 = new AuditLog();
        assertThat(auditLog1).isNotEqualTo(auditLog2);

        auditLog2.setId(auditLog1.getId());
        assertThat(auditLog1).isEqualTo(auditLog2);

        auditLog2 = getAuditLogSample2();
        assertThat(auditLog1).isNotEqualTo(auditLog2);
    }

    @Test
    void projectTest() {
        AuditLog auditLog = getAuditLogRandomSampleGenerator();
        Project projectBack = getProjectRandomSampleGenerator();

        auditLog.setProject(projectBack);
        assertThat(auditLog.getProject()).isEqualTo(projectBack);

        auditLog.project(null);
        assertThat(auditLog.getProject()).isNull();
    }
}
