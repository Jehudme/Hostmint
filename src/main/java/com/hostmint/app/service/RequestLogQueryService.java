package com.hostmint.app.service;

import com.hostmint.app.domain.*; // for static metamodels
import com.hostmint.app.domain.RequestLog;
import com.hostmint.app.repository.RequestLogRepository;
import com.hostmint.app.repository.search.RequestLogSearchRepository;
import com.hostmint.app.service.criteria.RequestLogCriteria;
import com.hostmint.app.service.dto.RequestLogDTO;
import com.hostmint.app.service.mapper.RequestLogMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link RequestLog} entities in the database.
 * The main input is a {@link RequestLogCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link RequestLogDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RequestLogQueryService extends QueryService<RequestLog> {

    private static final Logger LOG = LoggerFactory.getLogger(RequestLogQueryService.class);

    private final RequestLogRepository requestLogRepository;

    private final RequestLogMapper requestLogMapper;

    private final RequestLogSearchRepository requestLogSearchRepository;

    public RequestLogQueryService(
        RequestLogRepository requestLogRepository,
        RequestLogMapper requestLogMapper,
        RequestLogSearchRepository requestLogSearchRepository
    ) {
        this.requestLogRepository = requestLogRepository;
        this.requestLogMapper = requestLogMapper;
        this.requestLogSearchRepository = requestLogSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link RequestLogDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RequestLogDTO> findByCriteria(RequestLogCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<RequestLog> specification = createSpecification(criteria);
        return requestLogRepository.findAll(specification, page).map(requestLogMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RequestLogCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<RequestLog> specification = createSpecification(criteria);
        return requestLogRepository.count(specification);
    }

    /**
     * Function to convert {@link RequestLogCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<RequestLog> createSpecification(RequestLogCriteria criteria) {
        Specification<RequestLog> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), RequestLog_.id),
                buildStringSpecification(criteria.getCorrelationId(), RequestLog_.correlationId),
                buildSpecification(criteria.getMethod(), RequestLog_.method),
                buildStringSpecification(criteria.getPath(), RequestLog_.path),
                buildRangeSpecification(criteria.getStatusCode(), RequestLog_.statusCode),
                buildRangeSpecification(criteria.getDurationMs(), RequestLog_.durationMs),
                buildStringSpecification(criteria.getPrincipal(), RequestLog_.principal),
                buildStringSpecification(criteria.getIpAddress(), RequestLog_.ipAddress),
                buildStringSpecification(criteria.getErrorCode(), RequestLog_.errorCode),
                buildStringSpecification(criteria.getErrorMessage(), RequestLog_.errorMessage),
                buildRangeSpecification(criteria.getCreatedAt(), RequestLog_.createdAt),
                buildSpecification(criteria.getActorId(), root -> root.join(RequestLog_.actor, JoinType.LEFT).get(User_.id)),
                buildSpecification(criteria.getProjectId(), root -> root.join(RequestLog_.project, JoinType.LEFT).get(Project_.id))
            );
        }
        return specification;
    }
}
