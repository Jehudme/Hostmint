package com.hostmint.app.service;

import com.hostmint.app.domain.RequestLog;
import com.hostmint.app.repository.RequestLogRepository;
import com.hostmint.app.repository.search.RequestLogSearchRepository;
import com.hostmint.app.service.dto.RequestLogDTO;
import com.hostmint.app.service.mapper.RequestLogMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.hostmint.app.domain.RequestLog}.
 */
@Service
@Transactional
public class RequestLogService {

    private static final Logger LOG = LoggerFactory.getLogger(RequestLogService.class);

    private final RequestLogRepository requestLogRepository;

    private final RequestLogMapper requestLogMapper;

    private final RequestLogSearchRepository requestLogSearchRepository;

    public RequestLogService(
        RequestLogRepository requestLogRepository,
        RequestLogMapper requestLogMapper,
        RequestLogSearchRepository requestLogSearchRepository
    ) {
        this.requestLogRepository = requestLogRepository;
        this.requestLogMapper = requestLogMapper;
        this.requestLogSearchRepository = requestLogSearchRepository;
    }

    /**
     * Save a requestLog.
     *
     * @param requestLogDTO the entity to save.
     * @return the persisted entity.
     */
    public RequestLogDTO save(RequestLogDTO requestLogDTO) {
        LOG.debug("Request to save RequestLog : {}", requestLogDTO);
        RequestLog requestLog = requestLogMapper.toEntity(requestLogDTO);
        requestLog = requestLogRepository.save(requestLog);
        requestLogSearchRepository.index(requestLog);
        return requestLogMapper.toDto(requestLog);
    }

    /**
     * Update a requestLog.
     *
     * @param requestLogDTO the entity to save.
     * @return the persisted entity.
     */
    public RequestLogDTO update(RequestLogDTO requestLogDTO) {
        LOG.debug("Request to update RequestLog : {}", requestLogDTO);
        RequestLog requestLog = requestLogMapper.toEntity(requestLogDTO);
        requestLog = requestLogRepository.save(requestLog);
        requestLogSearchRepository.index(requestLog);
        return requestLogMapper.toDto(requestLog);
    }

    /**
     * Partially update a requestLog.
     *
     * @param requestLogDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RequestLogDTO> partialUpdate(RequestLogDTO requestLogDTO) {
        LOG.debug("Request to partially update RequestLog : {}", requestLogDTO);

        return requestLogRepository
            .findById(requestLogDTO.getId())
            .map(existingRequestLog -> {
                requestLogMapper.partialUpdate(existingRequestLog, requestLogDTO);

                return existingRequestLog;
            })
            .map(requestLogRepository::save)
            .map(savedRequestLog -> {
                requestLogSearchRepository.index(savedRequestLog);
                return savedRequestLog;
            })
            .map(requestLogMapper::toDto);
    }

    /**
     * Get all the requestLogs with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<RequestLogDTO> findAllWithEagerRelationships(Pageable pageable) {
        return requestLogRepository.findAllWithEagerRelationships(pageable).map(requestLogMapper::toDto);
    }

    /**
     * Get one requestLog by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RequestLogDTO> findOne(Long id) {
        LOG.debug("Request to get RequestLog : {}", id);
        return requestLogRepository.findOneWithEagerRelationships(id).map(requestLogMapper::toDto);
    }

    /**
     * Delete the requestLog by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete RequestLog : {}", id);
        requestLogRepository.deleteById(id);
        requestLogSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the requestLog corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<RequestLogDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of RequestLogs for query {}", query);
        return requestLogSearchRepository.search(query, pageable).map(requestLogMapper::toDto);
    }
}
