package com.hostmint.app.service.primary;

import com.hostmint.app.repository.AuditLogRepository;
import com.hostmint.app.repository.search.AuditLogSearchRepository;
import com.hostmint.app.service.AuditLogService;
import com.hostmint.app.service.mapper.AuditLogMapper;
import org.springframework.context.annotation.Primary;

@Primary
public class PrimaryAuditService extends AuditLogService {

    public PrimaryAuditService(
        AuditLogRepository auditLogRepository,
        AuditLogMapper auditLogMapper,
        AuditLogSearchRepository auditLogSearchRepository
    ) {
        super(auditLogRepository, auditLogMapper, auditLogSearchRepository);
    }
}
