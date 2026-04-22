package com.hostmint.app.aop.audit;

import com.hostmint.app.domain.enumeration.LogLevel;
import com.hostmint.app.service.InternalAuditService;
import com.hostmint.app.service.dto.ProjectDTO;
import java.util.UUID;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {

    private static final Logger LOG = LoggerFactory.getLogger(AuditAspect.class);

    private final InternalAuditService auditService;
    private final ExpressionParser parser = new SpelExpressionParser();

    public AuditAspect(InternalAuditService auditService) {
        this.auditService = auditService;
    }

    @Around("@annotation(auditAnnotation)")
    public Object audit(ProceedingJoinPoint joinPoint, Audit auditAnnotation) throws Throwable {
        // 1. Prepare initial context with method arguments
        StandardEvaluationContext context = getContext(joinPoint);

        Object result;
        try {
            // 2. Execute the actual business logic
            result = joinPoint.proceed();

            // 3. Add the result to the context so we can use #result in the annotation
            context.setVariable("result", result);

            // 4. Resolve dynamic values (Success Case)
            String action = auditAnnotation.action();
            String entityName = resolve(auditAnnotation.entity(), context, String.class, "System");
            String message = resolve(auditAnnotation.message(), context, String.class, "Success");
            String metadata = resolve(auditAnnotation.metadata(), context, String.class, null);
            UUID entityId = resolveIdAsUuid(auditAnnotation.entityId(), context);
            ProjectDTO project = resolve(auditAnnotation.project(), context, ProjectDTO.class, null);

            // 5. Log Success to Database
            auditService.log(action, entityName, entityId, auditAnnotation.level(), message, project, metadata);

            return result;
        } catch (Throwable e) {
            // 6. LOG ERROR TO CONSOLE IMMEDIATELY
            LOG.error(
                "AUDIT FAILURE: Action [{}] on Entity [{}] failed. Reason: {}",
                auditAnnotation.action(),
                auditAnnotation.entity(),
                e.getMessage(),
                e // Prints full stack trace
            );

            // 7. Resolve values for Failure Log (Note: #result is null here)
            String action = auditAnnotation.action() + "_FAILED";
            String entityName = resolve(auditAnnotation.entity(), context, String.class, "System");
            String message = "CRITICAL ERROR: " + e.getMessage() + " | " + resolve(auditAnnotation.message(), context, String.class, "");
            UUID entityId = resolveIdAsUuid(auditAnnotation.entityId(), context);
            ProjectDTO project = resolve(auditAnnotation.project(), context, ProjectDTO.class, null);

            // 8. Log Failure to Database
            auditService.log(action, entityName, entityId, LogLevel.ERROR, message, project, null);

            // 9. Rethrow so the transaction rolls back correctly
            throw e;
        }
    }

    /**
     * Resolves an ID from SpEL and safely converts Long/String to UUID for the Audit Service.
     */
    private UUID resolveIdAsUuid(String expression, StandardEvaluationContext context) {
        if (expression == null || expression.isEmpty()) return null;
        try {
            Object val = parser.parseExpression(expression).getValue(context);
            if (val instanceof UUID) return (UUID) val;
            if (val instanceof Long) return new UUID(0L, (Long) val);
            if (val instanceof String) return UUID.fromString((String) val);
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private StandardEvaluationContext getContext(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        StandardEvaluationContext context = new StandardEvaluationContext();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        }
        return context;
    }

    private <T> T resolve(String expression, StandardEvaluationContext context, Class<T> clazz, T defaultValue) {
        if (expression == null || expression.isEmpty()) return defaultValue;
        try {
            T value = parser.parseExpression(expression).getValue(context, clazz);
            return value != null ? value : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
