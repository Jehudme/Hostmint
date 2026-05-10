package com.hostmint.app.service.primary;

import com.hostmint.app.repository.RequestLogRepository;
import com.hostmint.app.repository.search.RequestLogSearchRepository;
import com.hostmint.app.service.RequestLogService;
import com.hostmint.app.service.mapper.RequestLogMapper;
import org.springframework.context.annotation.Primary;

@Primary
public class PrimaryRequestLogService extends RequestLogService {

    public PrimaryRequestLogService(
        RequestLogRepository requestLogRepository,
        RequestLogMapper requestLogMapper,
        RequestLogSearchRepository requestLogSearchRepository
    ) {
        super(requestLogRepository, requestLogMapper, requestLogSearchRepository);
    }
}
