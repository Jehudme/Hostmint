package com.hostmint.app.service.primary;

import com.hostmint.app.repository.AuditLogRepository;
import com.hostmint.app.repository.search.AuditLogSearchRepository;
import com.hostmint.app.service.AuditLogService;
import com.hostmint.app.service.dto.AuditLogDTO;
import com.hostmint.app.service.mapper.AuditLogMapper;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class PrimaryAuditService extends AuditLogService {

    public PrimaryAuditService(
        AuditLogRepository auditLogRepository,
        AuditLogMapper auditLogMapper,
        AuditLogSearchRepository auditLogSearchRepository
    ) {
        super(auditLogRepository, auditLogMapper, auditLogSearchRepository);
    }

    @Override
    public AuditLogDTO update(AuditLogDTO auditLogDTO) {
        throw new UnsupportedOperationException("Security Violation: Audit logs are immutable and cannot be updated.");
    }

    @Override
    public Optional<AuditLogDTO> partialUpdate(AuditLogDTO auditLogDTO) {
        throw new UnsupportedOperationException("Security Violation: Audit logs are immutable and cannot be modified.");
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException("Security Violation: Audit logs are permanent and cannot be deleted.");
    }
}
