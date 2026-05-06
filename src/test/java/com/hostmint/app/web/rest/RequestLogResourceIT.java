package com.hostmint.app.web.rest;

import static com.hostmint.app.domain.RequestLogAsserts.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hostmint.app.IntegrationTest;
import com.hostmint.app.domain.Project;
import com.hostmint.app.domain.RequestLog;
import com.hostmint.app.domain.User;
import com.hostmint.app.domain.enumeration.HttpMethod;
import com.hostmint.app.repository.RequestLogRepository;
import com.hostmint.app.repository.UserRepository;
import com.hostmint.app.repository.search.RequestLogSearchRepository;
import com.hostmint.app.service.RequestLogService;
import com.hostmint.app.service.mapper.RequestLogMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link RequestLogResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class RequestLogResourceIT {

    private static final String DEFAULT_CORRELATION_ID = "AAAAAAAAAA";
    private static final String UPDATED_CORRELATION_ID = "BBBBBBBBBB";

    private static final HttpMethod DEFAULT_METHOD = HttpMethod.GET;
    private static final HttpMethod UPDATED_METHOD = HttpMethod.POST;

    private static final String DEFAULT_PATH = "AAAAAAAAAA";
    private static final String UPDATED_PATH = "BBBBBBBBBB";

    private static final Integer DEFAULT_STATUS_CODE = 100;
    private static final Integer UPDATED_STATUS_CODE = 101;
    private static final Integer SMALLER_STATUS_CODE = 100 - 1;

    private static final Long DEFAULT_DURATION_MS = 0L;
    private static final Long UPDATED_DURATION_MS = 1L;
    private static final Long SMALLER_DURATION_MS = 0L - 1L;

    private static final String DEFAULT_PRINCIPAL = "AAAAAAAAAA";
    private static final String UPDATED_PRINCIPAL = "BBBBBBBBBB";

    private static final String DEFAULT_IP_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_IP_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_ERROR_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_ERROR_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_MESSAGE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/request-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/request-logs/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RequestLogRepository requestLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private RequestLogRepository requestLogRepositoryMock;

    @Autowired
    private RequestLogMapper requestLogMapper;

    @Mock
    private RequestLogService requestLogServiceMock;

    @Autowired
    private RequestLogSearchRepository requestLogSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRequestLogMockMvc;

    private RequestLog requestLog;

    private RequestLog insertedRequestLog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RequestLog createEntity() {
        return new RequestLog()
            .correlationId(DEFAULT_CORRELATION_ID)
            .method(DEFAULT_METHOD)
            .path(DEFAULT_PATH)
            .statusCode(DEFAULT_STATUS_CODE)
            .durationMs(DEFAULT_DURATION_MS)
            .principal(DEFAULT_PRINCIPAL)
            .ipAddress(DEFAULT_IP_ADDRESS)
            .errorCode(DEFAULT_ERROR_CODE)
            .errorMessage(DEFAULT_ERROR_MESSAGE)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RequestLog createUpdatedEntity() {
        return new RequestLog()
            .correlationId(UPDATED_CORRELATION_ID)
            .method(UPDATED_METHOD)
            .path(UPDATED_PATH)
            .statusCode(UPDATED_STATUS_CODE)
            .durationMs(UPDATED_DURATION_MS)
            .principal(UPDATED_PRINCIPAL)
            .ipAddress(UPDATED_IP_ADDRESS)
            .errorCode(UPDATED_ERROR_CODE)
            .errorMessage(UPDATED_ERROR_MESSAGE)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        requestLog = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedRequestLog != null) {
            requestLogRepository.delete(insertedRequestLog);
            requestLogSearchRepository.delete(insertedRequestLog);
            insertedRequestLog = null;
        }
    }

    @Test
    @Transactional
    void getAllRequestLogs() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList
        restRequestLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(requestLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].correlationId").value(hasItem(DEFAULT_CORRELATION_ID)))
            .andExpect(jsonPath("$.[*].method").value(hasItem(DEFAULT_METHOD.toString())))
            .andExpect(jsonPath("$.[*].path").value(hasItem(DEFAULT_PATH)))
            .andExpect(jsonPath("$.[*].statusCode").value(hasItem(DEFAULT_STATUS_CODE)))
            .andExpect(jsonPath("$.[*].durationMs").value(hasItem(DEFAULT_DURATION_MS.intValue())))
            .andExpect(jsonPath("$.[*].principal").value(hasItem(DEFAULT_PRINCIPAL)))
            .andExpect(jsonPath("$.[*].ipAddress").value(hasItem(DEFAULT_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].errorCode").value(hasItem(DEFAULT_ERROR_CODE)))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRequestLogsWithEagerRelationshipsIsEnabled() throws Exception {
        when(requestLogServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRequestLogMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(requestLogServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRequestLogsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(requestLogServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRequestLogMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(requestLogRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getRequestLog() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get the requestLog
        restRequestLogMockMvc
            .perform(get(ENTITY_API_URL_ID, requestLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(requestLog.getId().intValue()))
            .andExpect(jsonPath("$.correlationId").value(DEFAULT_CORRELATION_ID))
            .andExpect(jsonPath("$.method").value(DEFAULT_METHOD.toString()))
            .andExpect(jsonPath("$.path").value(DEFAULT_PATH))
            .andExpect(jsonPath("$.statusCode").value(DEFAULT_STATUS_CODE))
            .andExpect(jsonPath("$.durationMs").value(DEFAULT_DURATION_MS.intValue()))
            .andExpect(jsonPath("$.principal").value(DEFAULT_PRINCIPAL))
            .andExpect(jsonPath("$.ipAddress").value(DEFAULT_IP_ADDRESS))
            .andExpect(jsonPath("$.errorCode").value(DEFAULT_ERROR_CODE))
            .andExpect(jsonPath("$.errorMessage").value(DEFAULT_ERROR_MESSAGE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getRequestLogsByIdFiltering() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        Long id = requestLog.getId();

        defaultRequestLogFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultRequestLogFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultRequestLogFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRequestLogsByCorrelationIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where correlationId equals to
        defaultRequestLogFiltering("correlationId.equals=" + DEFAULT_CORRELATION_ID, "correlationId.equals=" + UPDATED_CORRELATION_ID);
    }

    @Test
    @Transactional
    void getAllRequestLogsByCorrelationIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where correlationId in
        defaultRequestLogFiltering(
            "correlationId.in=" + DEFAULT_CORRELATION_ID + "," + UPDATED_CORRELATION_ID,
            "correlationId.in=" + UPDATED_CORRELATION_ID
        );
    }

    @Test
    @Transactional
    void getAllRequestLogsByCorrelationIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where correlationId is not null
        defaultRequestLogFiltering("correlationId.specified=true", "correlationId.specified=false");
    }

    @Test
    @Transactional
    void getAllRequestLogsByCorrelationIdContainsSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where correlationId contains
        defaultRequestLogFiltering("correlationId.contains=" + DEFAULT_CORRELATION_ID, "correlationId.contains=" + UPDATED_CORRELATION_ID);
    }

    @Test
    @Transactional
    void getAllRequestLogsByCorrelationIdNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where correlationId does not contain
        defaultRequestLogFiltering(
            "correlationId.doesNotContain=" + UPDATED_CORRELATION_ID,
            "correlationId.doesNotContain=" + DEFAULT_CORRELATION_ID
        );
    }

    @Test
    @Transactional
    void getAllRequestLogsByMethodIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where method equals to
        defaultRequestLogFiltering("method.equals=" + DEFAULT_METHOD, "method.equals=" + UPDATED_METHOD);
    }

    @Test
    @Transactional
    void getAllRequestLogsByMethodIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where method in
        defaultRequestLogFiltering("method.in=" + DEFAULT_METHOD + "," + UPDATED_METHOD, "method.in=" + UPDATED_METHOD);
    }

    @Test
    @Transactional
    void getAllRequestLogsByMethodIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where method is not null
        defaultRequestLogFiltering("method.specified=true", "method.specified=false");
    }

    @Test
    @Transactional
    void getAllRequestLogsByPathIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where path equals to
        defaultRequestLogFiltering("path.equals=" + DEFAULT_PATH, "path.equals=" + UPDATED_PATH);
    }

    @Test
    @Transactional
    void getAllRequestLogsByPathIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where path in
        defaultRequestLogFiltering("path.in=" + DEFAULT_PATH + "," + UPDATED_PATH, "path.in=" + UPDATED_PATH);
    }

    @Test
    @Transactional
    void getAllRequestLogsByPathIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where path is not null
        defaultRequestLogFiltering("path.specified=true", "path.specified=false");
    }

    @Test
    @Transactional
    void getAllRequestLogsByPathContainsSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where path contains
        defaultRequestLogFiltering("path.contains=" + DEFAULT_PATH, "path.contains=" + UPDATED_PATH);
    }

    @Test
    @Transactional
    void getAllRequestLogsByPathNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where path does not contain
        defaultRequestLogFiltering("path.doesNotContain=" + UPDATED_PATH, "path.doesNotContain=" + DEFAULT_PATH);
    }

    @Test
    @Transactional
    void getAllRequestLogsByStatusCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where statusCode equals to
        defaultRequestLogFiltering("statusCode.equals=" + DEFAULT_STATUS_CODE, "statusCode.equals=" + UPDATED_STATUS_CODE);
    }

    @Test
    @Transactional
    void getAllRequestLogsByStatusCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where statusCode in
        defaultRequestLogFiltering(
            "statusCode.in=" + DEFAULT_STATUS_CODE + "," + UPDATED_STATUS_CODE,
            "statusCode.in=" + UPDATED_STATUS_CODE
        );
    }

    @Test
    @Transactional
    void getAllRequestLogsByStatusCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where statusCode is not null
        defaultRequestLogFiltering("statusCode.specified=true", "statusCode.specified=false");
    }

    @Test
    @Transactional
    void getAllRequestLogsByStatusCodeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where statusCode is greater than or equal to
        defaultRequestLogFiltering(
            "statusCode.greaterThanOrEqual=" + DEFAULT_STATUS_CODE,
            "statusCode.greaterThanOrEqual=" + (DEFAULT_STATUS_CODE + 1)
        );
    }

    @Test
    @Transactional
    void getAllRequestLogsByStatusCodeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where statusCode is less than or equal to
        defaultRequestLogFiltering(
            "statusCode.lessThanOrEqual=" + DEFAULT_STATUS_CODE,
            "statusCode.lessThanOrEqual=" + SMALLER_STATUS_CODE
        );
    }

    @Test
    @Transactional
    void getAllRequestLogsByStatusCodeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where statusCode is less than
        defaultRequestLogFiltering("statusCode.lessThan=" + (DEFAULT_STATUS_CODE + 1), "statusCode.lessThan=" + DEFAULT_STATUS_CODE);
    }

    @Test
    @Transactional
    void getAllRequestLogsByStatusCodeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where statusCode is greater than
        defaultRequestLogFiltering("statusCode.greaterThan=" + SMALLER_STATUS_CODE, "statusCode.greaterThan=" + DEFAULT_STATUS_CODE);
    }

    @Test
    @Transactional
    void getAllRequestLogsByDurationMsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where durationMs equals to
        defaultRequestLogFiltering("durationMs.equals=" + DEFAULT_DURATION_MS, "durationMs.equals=" + UPDATED_DURATION_MS);
    }

    @Test
    @Transactional
    void getAllRequestLogsByDurationMsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where durationMs in
        defaultRequestLogFiltering(
            "durationMs.in=" + DEFAULT_DURATION_MS + "," + UPDATED_DURATION_MS,
            "durationMs.in=" + UPDATED_DURATION_MS
        );
    }

    @Test
    @Transactional
    void getAllRequestLogsByDurationMsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where durationMs is not null
        defaultRequestLogFiltering("durationMs.specified=true", "durationMs.specified=false");
    }

    @Test
    @Transactional
    void getAllRequestLogsByDurationMsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where durationMs is greater than or equal to
        defaultRequestLogFiltering(
            "durationMs.greaterThanOrEqual=" + DEFAULT_DURATION_MS,
            "durationMs.greaterThanOrEqual=" + UPDATED_DURATION_MS
        );
    }

    @Test
    @Transactional
    void getAllRequestLogsByDurationMsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where durationMs is less than or equal to
        defaultRequestLogFiltering(
            "durationMs.lessThanOrEqual=" + DEFAULT_DURATION_MS,
            "durationMs.lessThanOrEqual=" + SMALLER_DURATION_MS
        );
    }

    @Test
    @Transactional
    void getAllRequestLogsByDurationMsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where durationMs is less than
        defaultRequestLogFiltering("durationMs.lessThan=" + UPDATED_DURATION_MS, "durationMs.lessThan=" + DEFAULT_DURATION_MS);
    }

    @Test
    @Transactional
    void getAllRequestLogsByDurationMsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where durationMs is greater than
        defaultRequestLogFiltering("durationMs.greaterThan=" + SMALLER_DURATION_MS, "durationMs.greaterThan=" + DEFAULT_DURATION_MS);
    }

    @Test
    @Transactional
    void getAllRequestLogsByPrincipalIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where principal equals to
        defaultRequestLogFiltering("principal.equals=" + DEFAULT_PRINCIPAL, "principal.equals=" + UPDATED_PRINCIPAL);
    }

    @Test
    @Transactional
    void getAllRequestLogsByPrincipalIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where principal in
        defaultRequestLogFiltering("principal.in=" + DEFAULT_PRINCIPAL + "," + UPDATED_PRINCIPAL, "principal.in=" + UPDATED_PRINCIPAL);
    }

    @Test
    @Transactional
    void getAllRequestLogsByPrincipalIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where principal is not null
        defaultRequestLogFiltering("principal.specified=true", "principal.specified=false");
    }

    @Test
    @Transactional
    void getAllRequestLogsByPrincipalContainsSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where principal contains
        defaultRequestLogFiltering("principal.contains=" + DEFAULT_PRINCIPAL, "principal.contains=" + UPDATED_PRINCIPAL);
    }

    @Test
    @Transactional
    void getAllRequestLogsByPrincipalNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where principal does not contain
        defaultRequestLogFiltering("principal.doesNotContain=" + UPDATED_PRINCIPAL, "principal.doesNotContain=" + DEFAULT_PRINCIPAL);
    }

    @Test
    @Transactional
    void getAllRequestLogsByIpAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where ipAddress equals to
        defaultRequestLogFiltering("ipAddress.equals=" + DEFAULT_IP_ADDRESS, "ipAddress.equals=" + UPDATED_IP_ADDRESS);
    }

    @Test
    @Transactional
    void getAllRequestLogsByIpAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where ipAddress in
        defaultRequestLogFiltering("ipAddress.in=" + DEFAULT_IP_ADDRESS + "," + UPDATED_IP_ADDRESS, "ipAddress.in=" + UPDATED_IP_ADDRESS);
    }

    @Test
    @Transactional
    void getAllRequestLogsByIpAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where ipAddress is not null
        defaultRequestLogFiltering("ipAddress.specified=true", "ipAddress.specified=false");
    }

    @Test
    @Transactional
    void getAllRequestLogsByIpAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where ipAddress contains
        defaultRequestLogFiltering("ipAddress.contains=" + DEFAULT_IP_ADDRESS, "ipAddress.contains=" + UPDATED_IP_ADDRESS);
    }

    @Test
    @Transactional
    void getAllRequestLogsByIpAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where ipAddress does not contain
        defaultRequestLogFiltering("ipAddress.doesNotContain=" + UPDATED_IP_ADDRESS, "ipAddress.doesNotContain=" + DEFAULT_IP_ADDRESS);
    }

    @Test
    @Transactional
    void getAllRequestLogsByErrorCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where errorCode equals to
        defaultRequestLogFiltering("errorCode.equals=" + DEFAULT_ERROR_CODE, "errorCode.equals=" + UPDATED_ERROR_CODE);
    }

    @Test
    @Transactional
    void getAllRequestLogsByErrorCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where errorCode in
        defaultRequestLogFiltering("errorCode.in=" + DEFAULT_ERROR_CODE + "," + UPDATED_ERROR_CODE, "errorCode.in=" + UPDATED_ERROR_CODE);
    }

    @Test
    @Transactional
    void getAllRequestLogsByErrorCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where errorCode is not null
        defaultRequestLogFiltering("errorCode.specified=true", "errorCode.specified=false");
    }

    @Test
    @Transactional
    void getAllRequestLogsByErrorCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where errorCode contains
        defaultRequestLogFiltering("errorCode.contains=" + DEFAULT_ERROR_CODE, "errorCode.contains=" + UPDATED_ERROR_CODE);
    }

    @Test
    @Transactional
    void getAllRequestLogsByErrorCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where errorCode does not contain
        defaultRequestLogFiltering("errorCode.doesNotContain=" + UPDATED_ERROR_CODE, "errorCode.doesNotContain=" + DEFAULT_ERROR_CODE);
    }

    @Test
    @Transactional
    void getAllRequestLogsByErrorMessageIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where errorMessage equals to
        defaultRequestLogFiltering("errorMessage.equals=" + DEFAULT_ERROR_MESSAGE, "errorMessage.equals=" + UPDATED_ERROR_MESSAGE);
    }

    @Test
    @Transactional
    void getAllRequestLogsByErrorMessageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where errorMessage in
        defaultRequestLogFiltering(
            "errorMessage.in=" + DEFAULT_ERROR_MESSAGE + "," + UPDATED_ERROR_MESSAGE,
            "errorMessage.in=" + UPDATED_ERROR_MESSAGE
        );
    }

    @Test
    @Transactional
    void getAllRequestLogsByErrorMessageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where errorMessage is not null
        defaultRequestLogFiltering("errorMessage.specified=true", "errorMessage.specified=false");
    }

    @Test
    @Transactional
    void getAllRequestLogsByErrorMessageContainsSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where errorMessage contains
        defaultRequestLogFiltering("errorMessage.contains=" + DEFAULT_ERROR_MESSAGE, "errorMessage.contains=" + UPDATED_ERROR_MESSAGE);
    }

    @Test
    @Transactional
    void getAllRequestLogsByErrorMessageNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where errorMessage does not contain
        defaultRequestLogFiltering(
            "errorMessage.doesNotContain=" + UPDATED_ERROR_MESSAGE,
            "errorMessage.doesNotContain=" + DEFAULT_ERROR_MESSAGE
        );
    }

    @Test
    @Transactional
    void getAllRequestLogsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where createdAt equals to
        defaultRequestLogFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllRequestLogsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where createdAt in
        defaultRequestLogFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllRequestLogsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);

        // Get all the requestLogList where createdAt is not null
        defaultRequestLogFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllRequestLogsByActorIsEqualToSomething() throws Exception {
        User actor;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            requestLogRepository.saveAndFlush(requestLog);
            actor = UserResourceIT.createEntity();
        } else {
            actor = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(actor);
        em.flush();
        requestLog.setActor(actor);
        requestLogRepository.saveAndFlush(requestLog);
        Long actorId = actor.getId();
        // Get all the requestLogList where actor equals to actorId
        defaultRequestLogShouldBeFound("actorId.equals=" + actorId);

        // Get all the requestLogList where actor equals to (actorId + 1)
        defaultRequestLogShouldNotBeFound("actorId.equals=" + (actorId + 1));
    }

    @Test
    @Transactional
    void getAllRequestLogsByProjectIsEqualToSomething() throws Exception {
        Project project;
        if (TestUtil.findAll(em, Project.class).isEmpty()) {
            requestLogRepository.saveAndFlush(requestLog);
            project = ProjectResourceIT.createEntity(em);
        } else {
            project = TestUtil.findAll(em, Project.class).get(0);
        }
        em.persist(project);
        em.flush();
        requestLog.setProject(project);
        requestLogRepository.saveAndFlush(requestLog);
        Long projectId = project.getId();
        // Get all the requestLogList where project equals to projectId
        defaultRequestLogShouldBeFound("projectId.equals=" + projectId);

        // Get all the requestLogList where project equals to (projectId + 1)
        defaultRequestLogShouldNotBeFound("projectId.equals=" + (projectId + 1));
    }

    private void defaultRequestLogFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultRequestLogShouldBeFound(shouldBeFound);
        defaultRequestLogShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRequestLogShouldBeFound(String filter) throws Exception {
        restRequestLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(requestLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].correlationId").value(hasItem(DEFAULT_CORRELATION_ID)))
            .andExpect(jsonPath("$.[*].method").value(hasItem(DEFAULT_METHOD.toString())))
            .andExpect(jsonPath("$.[*].path").value(hasItem(DEFAULT_PATH)))
            .andExpect(jsonPath("$.[*].statusCode").value(hasItem(DEFAULT_STATUS_CODE)))
            .andExpect(jsonPath("$.[*].durationMs").value(hasItem(DEFAULT_DURATION_MS.intValue())))
            .andExpect(jsonPath("$.[*].principal").value(hasItem(DEFAULT_PRINCIPAL)))
            .andExpect(jsonPath("$.[*].ipAddress").value(hasItem(DEFAULT_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].errorCode").value(hasItem(DEFAULT_ERROR_CODE)))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restRequestLogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRequestLogShouldNotBeFound(String filter) throws Exception {
        restRequestLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRequestLogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRequestLog() throws Exception {
        // Get the requestLog
        restRequestLogMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void searchRequestLog() throws Exception {
        // Initialize the database
        insertedRequestLog = requestLogRepository.saveAndFlush(requestLog);
        requestLogSearchRepository.save(requestLog);

        // Search the requestLog
        restRequestLogMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + requestLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(requestLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].correlationId").value(hasItem(DEFAULT_CORRELATION_ID)))
            .andExpect(jsonPath("$.[*].method").value(hasItem(DEFAULT_METHOD.toString())))
            .andExpect(jsonPath("$.[*].path").value(hasItem(DEFAULT_PATH)))
            .andExpect(jsonPath("$.[*].statusCode").value(hasItem(DEFAULT_STATUS_CODE)))
            .andExpect(jsonPath("$.[*].durationMs").value(hasItem(DEFAULT_DURATION_MS.intValue())))
            .andExpect(jsonPath("$.[*].principal").value(hasItem(DEFAULT_PRINCIPAL)))
            .andExpect(jsonPath("$.[*].ipAddress").value(hasItem(DEFAULT_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].errorCode").value(hasItem(DEFAULT_ERROR_CODE)))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return requestLogRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected RequestLog getPersistedRequestLog(RequestLog requestLog) {
        return requestLogRepository.findById(requestLog.getId()).orElseThrow();
    }

    protected void assertPersistedRequestLogToMatchAllProperties(RequestLog expectedRequestLog) {
        assertRequestLogAllPropertiesEquals(expectedRequestLog, getPersistedRequestLog(expectedRequestLog));
    }

    protected void assertPersistedRequestLogToMatchUpdatableProperties(RequestLog expectedRequestLog) {
        assertRequestLogAllUpdatablePropertiesEquals(expectedRequestLog, getPersistedRequestLog(expectedRequestLog));
    }
}
