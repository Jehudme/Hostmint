package com.hostmint.app.service;

import com.hostmint.app.domain.*; // for static metamodels
import com.hostmint.app.domain.AuditLog;
import com.hostmint.app.repository.AuditLogRepository;
import com.hostmint.app.repository.search.AuditLogSearchRepository;
import com.hostmint.app.service.criteria.AuditLogCriteria;
import com.hostmint.app.service.dto.AuditLogDTO;
import com.hostmint.app.service.mapper.AuditLogMapper;
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
 * Service for executing complex queries for {@link AuditLog} entities in the database.
 * The main input is a {@link AuditLogCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link AuditLogDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AuditLogQueryService extends QueryService<AuditLog> {

    private static final Logger LOG = LoggerFactory.getLogger(AuditLogQueryService.class);

    private final AuditLogRepository auditLogRepository;

    private final AuditLogMapper auditLogMapper;

    private final AuditLogSearchRepository auditLogSearchRepository;

    public AuditLogQueryService(
        AuditLogRepository auditLogRepository,
        AuditLogMapper auditLogMapper,
        AuditLogSearchRepository auditLogSearchRepository
    ) {
        this.auditLogRepository = auditLogRepository;
        this.auditLogMapper = auditLogMapper;
        this.auditLogSearchRepository = auditLogSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link AuditLogDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AuditLogDTO> findByCriteria(AuditLogCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<AuditLog> specification = createSpecification(criteria);
        return auditLogRepository.findAll(specification, page).map(auditLogMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AuditLogCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<AuditLog> specification = createSpecification(criteria);
        return auditLogRepository.count(specification);
    }

    /**
     * Function to convert {@link AuditLogCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<AuditLog> createSpecification(AuditLogCriteria criteria) {
        Specification<AuditLog> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), AuditLog_.id),
                buildStringSpecification(criteria.getAction(), AuditLog_.action),
                buildStringSpecification(criteria.getEntityName(), AuditLog_.entityName),
                buildRangeSpecification(criteria.getEntityId(), AuditLog_.entityId),
                buildSpecification(criteria.getLevel(), AuditLog_.level),
                buildStringSpecification(criteria.getMessage(), AuditLog_.message),
                buildStringSpecification(criteria.getPrincipal(), AuditLog_.principal),
                buildStringSpecification(criteria.getCorrelationId(), AuditLog_.correlationId),
                buildStringSpecification(criteria.getIpAddress(), AuditLog_.ipAddress),
                buildStringSpecification(criteria.getUserAgent(), AuditLog_.userAgent),
                buildRangeSpecification(criteria.getCreatedAt(), AuditLog_.createdAt),
                buildSpecification(criteria.getActorId(), root -> root.join(AuditLog_.actor, JoinType.LEFT).get(User_.id)),
                buildSpecification(criteria.getProjectId(), root -> root.join(AuditLog_.project, JoinType.LEFT).get(Project_.id))
            );
        }
        return specification;
    }
}
