package com.hostmint.app.service.mapper;

import static com.hostmint.app.domain.RequestLogAsserts.*;
import static com.hostmint.app.domain.RequestLogTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RequestLogMapperTest {

    private RequestLogMapper requestLogMapper;

    @BeforeEach
    void setUp() {
        requestLogMapper = new RequestLogMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRequestLogSample1();
        var actual = requestLogMapper.toEntity(requestLogMapper.toDto(expected));
        assertRequestLogAllPropertiesEquals(expected, actual);
    }
}
