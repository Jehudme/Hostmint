package com.hostmint.app.service.criteria;

import com.hostmint.app.domain.enumeration.HttpMethod;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.hostmint.app.domain.RequestLog} entity. This class is used
 * in {@link com.hostmint.app.web.rest.RequestLogResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /request-logs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RequestLogCriteria implements Serializable, Criteria {

    /**
     * Class for filtering HttpMethod
     */
    public static class HttpMethodFilter extends Filter<HttpMethod> {

        public HttpMethodFilter() {}

        public HttpMethodFilter(HttpMethodFilter filter) {
            super(filter);
        }

        @Override
        public HttpMethodFilter copy() {
            return new HttpMethodFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter correlationId;

    private HttpMethodFilter method;

    private StringFilter path;

    private IntegerFilter statusCode;

    private LongFilter durationMs;

    private StringFilter principal;

    private StringFilter ipAddress;

    private StringFilter errorCode;

    private StringFilter errorMessage;

    private InstantFilter createdAt;

    private LongFilter actorId;

    private LongFilter projectId;

    private Boolean distinct;

    public RequestLogCriteria() {}

    public RequestLogCriteria(RequestLogCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.correlationId = other.optionalCorrelationId().map(StringFilter::copy).orElse(null);
        this.method = other.optionalMethod().map(HttpMethodFilter::copy).orElse(null);
        this.path = other.optionalPath().map(StringFilter::copy).orElse(null);
        this.statusCode = other.optionalStatusCode().map(IntegerFilter::copy).orElse(null);
        this.durationMs = other.optionalDurationMs().map(LongFilter::copy).orElse(null);
        this.principal = other.optionalPrincipal().map(StringFilter::copy).orElse(null);
        this.ipAddress = other.optionalIpAddress().map(StringFilter::copy).orElse(null);
        this.errorCode = other.optionalErrorCode().map(StringFilter::copy).orElse(null);
        this.errorMessage = other.optionalErrorMessage().map(StringFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.actorId = other.optionalActorId().map(LongFilter::copy).orElse(null);
        this.projectId = other.optionalProjectId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public RequestLogCriteria copy() {
        return new RequestLogCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getCorrelationId() {
        return correlationId;
    }

    public Optional<StringFilter> optionalCorrelationId() {
        return Optional.ofNullable(correlationId);
    }

    public StringFilter correlationId() {
        if (correlationId == null) {
            setCorrelationId(new StringFilter());
        }
        return correlationId;
    }

    public void setCorrelationId(StringFilter correlationId) {
        this.correlationId = correlationId;
    }

    public HttpMethodFilter getMethod() {
        return method;
    }

    public Optional<HttpMethodFilter> optionalMethod() {
        return Optional.ofNullable(method);
    }

    public HttpMethodFilter method() {
        if (method == null) {
            setMethod(new HttpMethodFilter());
        }
        return method;
    }

    public void setMethod(HttpMethodFilter method) {
        this.method = method;
    }

    public StringFilter getPath() {
        return path;
    }

    public Optional<StringFilter> optionalPath() {
        return Optional.ofNullable(path);
    }

    public StringFilter path() {
        if (path == null) {
            setPath(new StringFilter());
        }
        return path;
    }

    public void setPath(StringFilter path) {
        this.path = path;
    }

    public IntegerFilter getStatusCode() {
        return statusCode;
    }

    public Optional<IntegerFilter> optionalStatusCode() {
        return Optional.ofNullable(statusCode);
    }

    public IntegerFilter statusCode() {
        if (statusCode == null) {
            setStatusCode(new IntegerFilter());
        }
        return statusCode;
    }

    public void setStatusCode(IntegerFilter statusCode) {
        this.statusCode = statusCode;
    }

    public LongFilter getDurationMs() {
        return durationMs;
    }

    public Optional<LongFilter> optionalDurationMs() {
        return Optional.ofNullable(durationMs);
    }

    public LongFilter durationMs() {
        if (durationMs == null) {
            setDurationMs(new LongFilter());
        }
        return durationMs;
    }

    public void setDurationMs(LongFilter durationMs) {
        this.durationMs = durationMs;
    }

    public StringFilter getPrincipal() {
        return principal;
    }

    public Optional<StringFilter> optionalPrincipal() {
        return Optional.ofNullable(principal);
    }

    public StringFilter principal() {
        if (principal == null) {
            setPrincipal(new StringFilter());
        }
        return principal;
    }

    public void setPrincipal(StringFilter principal) {
        this.principal = principal;
    }

    public StringFilter getIpAddress() {
        return ipAddress;
    }

    public Optional<StringFilter> optionalIpAddress() {
        return Optional.ofNullable(ipAddress);
    }

    public StringFilter ipAddress() {
        if (ipAddress == null) {
            setIpAddress(new StringFilter());
        }
        return ipAddress;
    }

    public void setIpAddress(StringFilter ipAddress) {
        this.ipAddress = ipAddress;
    }

    public StringFilter getErrorCode() {
        return errorCode;
    }

    public Optional<StringFilter> optionalErrorCode() {
        return Optional.ofNullable(errorCode);
    }

    public StringFilter errorCode() {
        if (errorCode == null) {
            setErrorCode(new StringFilter());
        }
        return errorCode;
    }

    public void setErrorCode(StringFilter errorCode) {
        this.errorCode = errorCode;
    }

    public StringFilter getErrorMessage() {
        return errorMessage;
    }

    public Optional<StringFilter> optionalErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }

    public StringFilter errorMessage() {
        if (errorMessage == null) {
            setErrorMessage(new StringFilter());
        }
        return errorMessage;
    }

    public void setErrorMessage(StringFilter errorMessage) {
        this.errorMessage = errorMessage;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public LongFilter getActorId() {
        return actorId;
    }

    public Optional<LongFilter> optionalActorId() {
        return Optional.ofNullable(actorId);
    }

    public LongFilter actorId() {
        if (actorId == null) {
            setActorId(new LongFilter());
        }
        return actorId;
    }

    public void setActorId(LongFilter actorId) {
        this.actorId = actorId;
    }

    public LongFilter getProjectId() {
        return projectId;
    }

    public Optional<LongFilter> optionalProjectId() {
        return Optional.ofNullable(projectId);
    }

    public LongFilter projectId() {
        if (projectId == null) {
            setProjectId(new LongFilter());
        }
        return projectId;
    }

    public void setProjectId(LongFilter projectId) {
        this.projectId = projectId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RequestLogCriteria that = (RequestLogCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(correlationId, that.correlationId) &&
            Objects.equals(method, that.method) &&
            Objects.equals(path, that.path) &&
            Objects.equals(statusCode, that.statusCode) &&
            Objects.equals(durationMs, that.durationMs) &&
            Objects.equals(principal, that.principal) &&
            Objects.equals(ipAddress, that.ipAddress) &&
            Objects.equals(errorCode, that.errorCode) &&
            Objects.equals(errorMessage, that.errorMessage) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(actorId, that.actorId) &&
            Objects.equals(projectId, that.projectId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            correlationId,
            method,
            path,
            statusCode,
            durationMs,
            principal,
            ipAddress,
            errorCode,
            errorMessage,
            createdAt,
            actorId,
            projectId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RequestLogCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCorrelationId().map(f -> "correlationId=" + f + ", ").orElse("") +
            optionalMethod().map(f -> "method=" + f + ", ").orElse("") +
            optionalPath().map(f -> "path=" + f + ", ").orElse("") +
            optionalStatusCode().map(f -> "statusCode=" + f + ", ").orElse("") +
            optionalDurationMs().map(f -> "durationMs=" + f + ", ").orElse("") +
            optionalPrincipal().map(f -> "principal=" + f + ", ").orElse("") +
            optionalIpAddress().map(f -> "ipAddress=" + f + ", ").orElse("") +
            optionalErrorCode().map(f -> "errorCode=" + f + ", ").orElse("") +
            optionalErrorMessage().map(f -> "errorMessage=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalActorId().map(f -> "actorId=" + f + ", ").orElse("") +
            optionalProjectId().map(f -> "projectId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
