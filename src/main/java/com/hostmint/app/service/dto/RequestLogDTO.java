package com.hostmint.app.service.dto;

import com.hostmint.app.domain.enumeration.HttpMethod;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.hostmint.app.domain.RequestLog} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RequestLogDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 128)
    private String correlationId;

    @NotNull
    private HttpMethod method;

    @NotNull
    @Size(max = 500)
    private String path;

    @NotNull
    @Min(value = 100)
    @Max(value = 599)
    private Integer statusCode;

    @NotNull
    @Min(value = 0L)
    private Long durationMs;

    @Size(max = 255)
    private String principal;

    @Size(max = 64)
    private String ipAddress;

    @Size(max = 120)
    private String errorCode;

    @Size(max = 1000)
    private String errorMessage;

    private Instant createdAt;

    private UserDTO actor;

    private ProjectDTO project;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public UserDTO getActor() {
        return actor;
    }

    public void setActor(UserDTO actor) {
        this.actor = actor;
    }

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RequestLogDTO)) {
            return false;
        }

        RequestLogDTO requestLogDTO = (RequestLogDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, requestLogDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RequestLogDTO{" +
            "id=" + getId() +
            ", correlationId='" + getCorrelationId() + "'" +
            ", method='" + getMethod() + "'" +
            ", path='" + getPath() + "'" +
            ", statusCode=" + getStatusCode() +
            ", durationMs=" + getDurationMs() +
            ", principal='" + getPrincipal() + "'" +
            ", ipAddress='" + getIpAddress() + "'" +
            ", errorCode='" + getErrorCode() + "'" +
            ", errorMessage='" + getErrorMessage() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", actor=" + getActor() +
            ", project=" + getProject() +
            "}";
    }
}
