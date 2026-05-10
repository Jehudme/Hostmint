package com.hostmint.app.service.primary;

import com.hostmint.app.repository.RequestLogRepository;
import com.hostmint.app.repository.search.RequestLogSearchRepository;
import com.hostmint.app.service.RequestLogService;
import com.hostmint.app.service.dto.RequestLogDTO;
import com.hostmint.app.service.mapper.RequestLogMapper;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class PrimaryRequestLogService extends RequestLogService {

    public PrimaryRequestLogService(
        RequestLogRepository requestLogRepository,
        RequestLogMapper requestLogMapper,
        RequestLogSearchRepository requestLogSearchRepository
    ) {
        super(requestLogRepository, requestLogMapper, requestLogSearchRepository);
    }

    @Override
    public RequestLogDTO update(RequestLogDTO requestLogDTO) {
        throw new UnsupportedOperationException("Security Violation: Request logs are immutable and cannot be updated.");
    }

    @Override
    public Optional<RequestLogDTO> partialUpdate(RequestLogDTO requestLogDTO) {
        throw new UnsupportedOperationException("Security Violation: Request logs are immutable and cannot be modified.");
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException("Security Violation: Request logs are permanent and cannot be deleted.");
    }
}
