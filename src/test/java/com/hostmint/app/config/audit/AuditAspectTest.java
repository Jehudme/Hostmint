package com.hostmint.app.config.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hostmint.app.aop.audit.Audit;
import com.hostmint.app.domain.enumeration.LogLevel;
import com.hostmint.app.service.InternalAuditService;
import com.hostmint.app.service.dto.ProjectDTO;
import java.lang.reflect.Method;
import java.util.UUID;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuditAspectTest {

    @Mock
    private InternalAuditService internalAuditService;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    private AuditAspect auditAspect;

    @BeforeEach
    void setUp() {
        auditAspect = new AuditAspect(internalAuditService);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getParameterNames()).thenReturn(new String[] { "id" });
    }

    @Test
    void auditShouldCreateSuccessEventWithResolvedFields() throws Throwable {
        UUID id = UUID.randomUUID();
        ProjectDTO result = new ProjectDTO();
        result.setId(id);
        result.setName("Alpha");

        Audit auditAnnotation = getAuditAnnotation("successCase");
        when(joinPoint.getArgs()).thenReturn(new Object[] { id });
        when(joinPoint.proceed()).thenReturn(result);

        Object response = auditAspect.audit(joinPoint, auditAnnotation);

        assertThat(response).isSameAs(result);
        verify(internalAuditService).log(
            eq("PROJECT_CREATE"),
            eq("Alpha"),
            eq(id),
            eq(LogLevel.INFO),
            eq("Created project"),
            same(result),
            eq("meta:" + id)
        );
    }

    @Test
    void auditShouldCreateFailureEventAndRethrow() throws Throwable {
        UUID id = UUID.randomUUID();
        RuntimeException failure = new RuntimeException("boom");

        Audit auditAnnotation = getAuditAnnotation("failureCase");
        when(joinPoint.getArgs()).thenReturn(new Object[] { id });
        when(joinPoint.proceed()).thenThrow(failure);

        assertThatThrownBy(() -> auditAspect.audit(joinPoint, auditAnnotation)).isSameAs(failure);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(internalAuditService).log(
            eq("PROJECT_DELETE_FAILED"),
            eq("Project"),
            eq(id),
            eq(LogLevel.ERROR),
            messageCaptor.capture(),
            isNull(),
            isNull()
        );
        assertThat(messageCaptor.getValue()).contains("CRITICAL ERROR: boom").contains("Deleted project");
    }

    private Audit getAuditAnnotation(String methodName) throws NoSuchMethodException {
        Method method = AnnotatedMethods.class.getDeclaredMethod(methodName, UUID.class);
        return method.getAnnotation(Audit.class);
    }

    private static class AnnotatedMethods {

        @Audit(
            action = "PROJECT_CREATE",
            entity = "#result.name",
            entityId = "#result.id",
            level = LogLevel.INFO,
            message = "'Created project'",
            project = "#result",
            metadata = "'meta:' + #id"
        )
        ProjectDTO successCase(UUID id) {
            return null;
        }

        @Audit(action = "PROJECT_DELETE", entity = "'Project'", entityId = "#id", message = "'Deleted project'")
        void failureCase(UUID id) {}
    }
}
