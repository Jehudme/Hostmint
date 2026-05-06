package com.hostmint.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.hostmint.app.domain.RequestLog;
import com.hostmint.app.repository.RequestLogRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link RequestLog} entity.
 */
public interface RequestLogSearchRepository extends ElasticsearchRepository<RequestLog, Long>, RequestLogSearchRepositoryInternal {}

interface RequestLogSearchRepositoryInternal {
    Page<RequestLog> search(String query, Pageable pageable);

    Page<RequestLog> search(Query query);

    @Async
    void index(RequestLog entity);

    @Async
    void deleteFromIndexById(Long id);
}

class RequestLogSearchRepositoryInternalImpl implements RequestLogSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final RequestLogRepository repository;

    RequestLogSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, RequestLogRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<RequestLog> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<RequestLog> search(Query query) {
        SearchHits<RequestLog> searchHits = elasticsearchTemplate.search(query, RequestLog.class);
        List<RequestLog> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(RequestLog entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), RequestLog.class);
    }
}
