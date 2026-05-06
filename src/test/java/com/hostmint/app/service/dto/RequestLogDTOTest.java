package com.hostmint.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.hostmint.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RequestLogDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RequestLogDTO.class);
        RequestLogDTO requestLogDTO1 = new RequestLogDTO();
        requestLogDTO1.setId(1L);
        RequestLogDTO requestLogDTO2 = new RequestLogDTO();
        assertThat(requestLogDTO1).isNotEqualTo(requestLogDTO2);
        requestLogDTO2.setId(requestLogDTO1.getId());
        assertThat(requestLogDTO1).isEqualTo(requestLogDTO2);
        requestLogDTO2.setId(2L);
        assertThat(requestLogDTO1).isNotEqualTo(requestLogDTO2);
        requestLogDTO1.setId(null);
        assertThat(requestLogDTO1).isNotEqualTo(requestLogDTO2);
    }
}
