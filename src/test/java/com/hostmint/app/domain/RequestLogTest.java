package com.hostmint.app.domain;

import static com.hostmint.app.domain.ProjectTestSamples.*;
import static com.hostmint.app.domain.RequestLogTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.hostmint.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RequestLogTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RequestLog.class);
        RequestLog requestLog1 = getRequestLogSample1();
        RequestLog requestLog2 = new RequestLog();
        assertThat(requestLog1).isNotEqualTo(requestLog2);

        requestLog2.setId(requestLog1.getId());
        assertThat(requestLog1).isEqualTo(requestLog2);

        requestLog2 = getRequestLogSample2();
        assertThat(requestLog1).isNotEqualTo(requestLog2);
    }

    @Test
    void projectTest() {
        RequestLog requestLog = getRequestLogRandomSampleGenerator();
        Project projectBack = getProjectRandomSampleGenerator();

        requestLog.setProject(projectBack);
        assertThat(requestLog.getProject()).isEqualTo(projectBack);

        requestLog.project(null);
        assertThat(requestLog.getProject()).isNull();
    }
}
