package com.hostmint.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.hostmint.app.domain.Project;
import com.hostmint.app.repository.ProjectRepository;
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
 * Spring Data Elasticsearch repository for the {@link Project} entity.
 */
public interface ProjectSearchRepository extends ElasticsearchRepository<Project, Long>, ProjectSearchRepositoryInternal {}

interface ProjectSearchRepositoryInternal {
    Page<Project> search(String query, Pageable pageable);

    Page<Project> search(Query query);

    @Async
    void index(Project entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ProjectSearchRepositoryInternalImpl implements ProjectSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ProjectRepository repository;

    ProjectSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ProjectRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Project> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Project> search(Query query) {
        SearchHits<Project> searchHits = elasticsearchTemplate.search(query, Project.class);
        List<Project> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Project entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Project.class);
    }
}
