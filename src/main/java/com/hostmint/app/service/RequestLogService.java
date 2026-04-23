package com.hostmint.app.service;

import com.hostmint.app.service.dto.RequestLogDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.hostmint.app.domain.RequestLog}.
 */
public interface RequestLogService {
    /**
     * Save a requestLog.
     *
     * @param requestLogDTO the entity to save.
     * @return the persisted entity.
     */
    RequestLogDTO save(RequestLogDTO requestLogDTO);

    /**
     * Updates a requestLog.
     *
     * @param requestLogDTO the entity to update.
     * @return the persisted entity.
     */
    RequestLogDTO update(RequestLogDTO requestLogDTO);

    /**
     * Partially updates a requestLog.
     *
     * @param requestLogDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<RequestLogDTO> partialUpdate(RequestLogDTO requestLogDTO);

    /**
     * Get all the requestLogs with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<RequestLogDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" requestLog.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<RequestLogDTO> findOne(Long id);

    /**
     * Delete the "id" requestLog.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the requestLog corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<RequestLogDTO> search(String query, Pageable pageable);
}
