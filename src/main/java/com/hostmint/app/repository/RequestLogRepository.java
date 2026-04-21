package com.hostmint.app.repository;

import com.hostmint.app.domain.RequestLog;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RequestLog entity.
 */
@Repository
public interface RequestLogRepository extends JpaRepository<RequestLog, Long>, JpaSpecificationExecutor<RequestLog> {
    @Query("select requestLog from RequestLog requestLog where requestLog.actor.login = ?#{authentication.name}")
    List<RequestLog> findByActorIsCurrentUser();

    default Optional<RequestLog> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<RequestLog> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<RequestLog> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select requestLog from RequestLog requestLog left join fetch requestLog.actor left join fetch requestLog.project",
        countQuery = "select count(requestLog) from RequestLog requestLog"
    )
    Page<RequestLog> findAllWithToOneRelationships(Pageable pageable);

    @Query("select requestLog from RequestLog requestLog left join fetch requestLog.actor left join fetch requestLog.project")
    List<RequestLog> findAllWithToOneRelationships();

    @Query(
        "select requestLog from RequestLog requestLog left join fetch requestLog.actor left join fetch requestLog.project where requestLog.id =:id"
    )
    Optional<RequestLog> findOneWithToOneRelationships(@Param("id") Long id);
}
