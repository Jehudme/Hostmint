package com.hostmint.app.web.rest;

import com.hostmint.app.service.RequestLogQueryService;
import com.hostmint.app.service.RequestLogService;
import com.hostmint.app.service.criteria.RequestLogCriteria;
import com.hostmint.app.service.dto.RequestLogDTO;
import com.hostmint.app.web.rest.errors.ElasticsearchExceptionMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.hostmint.app.domain.RequestLog}.
 */
@RestController
@RequestMapping("/api/request-logs")
public class RequestLogResource {

    private static final Logger LOG = LoggerFactory.getLogger(RequestLogResource.class);

    private final RequestLogService requestLogService;

    private final RequestLogQueryService requestLogQueryService;

    public RequestLogResource(RequestLogService requestLogService, RequestLogQueryService requestLogQueryService) {
        this.requestLogService = requestLogService;
        this.requestLogQueryService = requestLogQueryService;
    }

    /**
     * {@code GET  /request-logs} : get all the Request Logs.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Request Logs in body.
     */
    @GetMapping("")
    public ResponseEntity<List<RequestLogDTO>> getAllRequestLogs(
        RequestLogCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get RequestLogs by criteria: {}", criteria);

        Page<RequestLogDTO> page = requestLogQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /request-logs/count} : count all the requestLogs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countRequestLogs(RequestLogCriteria criteria) {
        LOG.debug("REST request to count RequestLogs by criteria: {}", criteria);
        return ResponseEntity.ok().body(requestLogQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /request-logs/:id} : get the "id" requestLog.
     *
     * @param id the id of the requestLogDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the requestLogDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RequestLogDTO> getRequestLog(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RequestLog : {}", id);
        Optional<RequestLogDTO> requestLogDTO = requestLogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(requestLogDTO);
    }

    /**
     * {@code SEARCH  /request-logs/_search?query=:query} : search for the requestLog corresponding
     * to the query.
     *
     * @param query the query of the requestLog search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<RequestLogDTO>> searchRequestLogs(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of RequestLogs for query {}", query);
        try {
            Page<RequestLogDTO> page = requestLogService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
