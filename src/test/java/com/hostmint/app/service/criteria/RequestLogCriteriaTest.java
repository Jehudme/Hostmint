package com.hostmint.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class RequestLogCriteriaTest {

    @Test
    void newRequestLogCriteriaHasAllFiltersNullTest() {
        var requestLogCriteria = new RequestLogCriteria();
        assertThat(requestLogCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void requestLogCriteriaFluentMethodsCreatesFiltersTest() {
        var requestLogCriteria = new RequestLogCriteria();

        setAllFilters(requestLogCriteria);

        assertThat(requestLogCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void requestLogCriteriaCopyCreatesNullFilterTest() {
        var requestLogCriteria = new RequestLogCriteria();
        var copy = requestLogCriteria.copy();

        assertThat(requestLogCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(requestLogCriteria)
        );
    }

    @Test
    void requestLogCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var requestLogCriteria = new RequestLogCriteria();
        setAllFilters(requestLogCriteria);

        var copy = requestLogCriteria.copy();

        assertThat(requestLogCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(requestLogCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var requestLogCriteria = new RequestLogCriteria();

        assertThat(requestLogCriteria).hasToString("RequestLogCriteria{}");
    }

    private static void setAllFilters(RequestLogCriteria requestLogCriteria) {
        requestLogCriteria.id();
        requestLogCriteria.correlationId();
        requestLogCriteria.method();
        requestLogCriteria.path();
        requestLogCriteria.statusCode();
        requestLogCriteria.durationMs();
        requestLogCriteria.principal();
        requestLogCriteria.ipAddress();
        requestLogCriteria.errorCode();
        requestLogCriteria.errorMessage();
        requestLogCriteria.createdAt();
        requestLogCriteria.actorId();
        requestLogCriteria.projectId();
        requestLogCriteria.distinct();
    }

    private static Condition<RequestLogCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCorrelationId()) &&
                condition.apply(criteria.getMethod()) &&
                condition.apply(criteria.getPath()) &&
                condition.apply(criteria.getStatusCode()) &&
                condition.apply(criteria.getDurationMs()) &&
                condition.apply(criteria.getPrincipal()) &&
                condition.apply(criteria.getIpAddress()) &&
                condition.apply(criteria.getErrorCode()) &&
                condition.apply(criteria.getErrorMessage()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getActorId()) &&
                condition.apply(criteria.getProjectId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<RequestLogCriteria> copyFiltersAre(RequestLogCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCorrelationId(), copy.getCorrelationId()) &&
                condition.apply(criteria.getMethod(), copy.getMethod()) &&
                condition.apply(criteria.getPath(), copy.getPath()) &&
                condition.apply(criteria.getStatusCode(), copy.getStatusCode()) &&
                condition.apply(criteria.getDurationMs(), copy.getDurationMs()) &&
                condition.apply(criteria.getPrincipal(), copy.getPrincipal()) &&
                condition.apply(criteria.getIpAddress(), copy.getIpAddress()) &&
                condition.apply(criteria.getErrorCode(), copy.getErrorCode()) &&
                condition.apply(criteria.getErrorMessage(), copy.getErrorMessage()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getActorId(), copy.getActorId()) &&
                condition.apply(criteria.getProjectId(), copy.getProjectId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
