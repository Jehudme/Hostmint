package com.hostmint.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.hostmint.app.domain.AuditLog;
import com.hostmint.app.repository.AuditLogRepository;
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
 * Spring Data Elasticsearch repository for the {@link AuditLog} entity.
 */
public interface AuditLogSearchRepository extends ElasticsearchRepository<AuditLog, Long>, AuditLogSearchRepositoryInternal {}

interface AuditLogSearchRepositoryInternal {
    Page<AuditLog> search(String query, Pageable pageable);

    Page<AuditLog> search(Query query);

    @Async
    void index(AuditLog entity);

    @Async
    void deleteFromIndexById(Long id);
}

class AuditLogSearchRepositoryInternalImpl implements AuditLogSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final AuditLogRepository repository;

    AuditLogSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, AuditLogRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<AuditLog> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<AuditLog> search(Query query) {
        SearchHits<AuditLog> searchHits = elasticsearchTemplate.search(query, AuditLog.class);
        List<AuditLog> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(AuditLog entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), AuditLog.class);
    }
}
