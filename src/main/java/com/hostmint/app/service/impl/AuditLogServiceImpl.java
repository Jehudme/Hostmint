package com.hostmint.app.service.impl;

import com.hostmint.app.domain.AuditLog;
import com.hostmint.app.repository.AuditLogRepository;
import com.hostmint.app.repository.search.AuditLogSearchRepository;
import com.hostmint.app.service.AuditLogService;
import com.hostmint.app.service.dto.AuditLogDTO;
import com.hostmint.app.service.mapper.AuditLogMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.hostmint.app.domain.AuditLog}.
 */
@Service
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    private static final Logger LOG = LoggerFactory.getLogger(AuditLogServiceImpl.class);

    private final AuditLogRepository auditLogRepository;

    private final AuditLogMapper auditLogMapper;

    private final AuditLogSearchRepository auditLogSearchRepository;

    public AuditLogServiceImpl(
        AuditLogRepository auditLogRepository,
        AuditLogMapper auditLogMapper,
        AuditLogSearchRepository auditLogSearchRepository
    ) {
        this.auditLogRepository = auditLogRepository;
        this.auditLogMapper = auditLogMapper;
        this.auditLogSearchRepository = auditLogSearchRepository;
    }

    @Override
    public AuditLogDTO save(AuditLogDTO auditLogDTO) {
        LOG.debug("Request to save AuditLog : {}", auditLogDTO);
        AuditLog auditLog = auditLogMapper.toEntity(auditLogDTO);
        auditLog = auditLogRepository.save(auditLog);
        auditLogSearchRepository.index(auditLog);
        return auditLogMapper.toDto(auditLog);
    }

    @Override
    public AuditLogDTO update(AuditLogDTO auditLogDTO) {
        LOG.debug("Request to update AuditLog : {}", auditLogDTO);
        AuditLog auditLog = auditLogMapper.toEntity(auditLogDTO);
        auditLog = auditLogRepository.save(auditLog);
        auditLogSearchRepository.index(auditLog);
        return auditLogMapper.toDto(auditLog);
    }

    @Override
    public Optional<AuditLogDTO> partialUpdate(AuditLogDTO auditLogDTO) {
        LOG.debug("Request to partially update AuditLog : {}", auditLogDTO);

        return auditLogRepository
            .findById(auditLogDTO.getId())
            .map(existingAuditLog -> {
                auditLogMapper.partialUpdate(existingAuditLog, auditLogDTO);

                return existingAuditLog;
            })
            .map(auditLogRepository::save)
            .map(savedAuditLog -> {
                auditLogSearchRepository.index(savedAuditLog);
                return savedAuditLog;
            })
            .map(auditLogMapper::toDto);
    }

    public Page<AuditLogDTO> findAllWithEagerRelationships(Pageable pageable) {
        return auditLogRepository.findAllWithEagerRelationships(pageable).map(auditLogMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuditLogDTO> findOne(Long id) {
        LOG.debug("Request to get AuditLog : {}", id);
        return auditLogRepository.findOneWithEagerRelationships(id).map(auditLogMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete AuditLog : {}", id);
        auditLogRepository.deleteById(id);
        auditLogSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of AuditLogs for query {}", query);
        return auditLogSearchRepository.search(query, pageable).map(auditLogMapper::toDto);
    }
}
