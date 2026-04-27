package com.hostmint.app.config.audit;

import com.hostmint.app.aop.audit.Audit;
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
        StandardEvaluationContext context = getContext(joinPoint);

        Object result;
        try {
            result = joinPoint.proceed();
            context.setVariable("result", result);

            String action = auditAnnotation.action();
            String entityName = resolve(auditAnnotation.entity(), context, String.class, "System");
            String message = resolve(auditAnnotation.message(), context, String.class, "Success");
            String metadata = resolve(auditAnnotation.metadata(), context, String.class, null);
            UUID entityId = resolveIdAsUuid(auditAnnotation.entityId(), context);
            ProjectDTO project = resolve(auditAnnotation.project(), context, ProjectDTO.class, null);

            LogLevel resolvedLevel;
            try {
                resolvedLevel = LogLevel.valueOf(auditAnnotation.level());
            } catch (IllegalArgumentException e) {
                LOG.warn("Unknown audit level '{}' on action '{}', defaulting to INFO", auditAnnotation.level(), auditAnnotation.action());
                resolvedLevel = LogLevel.INFO;
            }
            auditService.log(action, entityName, entityId, resolvedLevel, message, project, metadata);

            return result;
        } catch (Throwable e) {
            LOG.error(
                "AUDIT FAILURE: Action [{}] on Entity [{}] failed. Reason: {}",
                auditAnnotation.action(),
                auditAnnotation.entity(),
                e.getMessage(),
                e
            );

            String action = auditAnnotation.action() + "_FAILED";
            String entityName = resolve(auditAnnotation.entity(), context, String.class, "System");
            String message = "CRITICAL ERROR: " + e.getMessage() + " | " + resolve(auditAnnotation.message(), context, String.class, "");
            UUID entityId = resolveIdAsUuid(auditAnnotation.entityId(), context);
            ProjectDTO project = resolve(auditAnnotation.project(), context, ProjectDTO.class, null);

            auditService.log(action, entityName, entityId, LogLevel.ERROR, message, project, null);
            throw e;
        }
    }

    private UUID resolveIdAsUuid(String expression, StandardEvaluationContext context) {
        if (expression == null || expression.isEmpty()) {
            return null;
        }
        try {
            Object val = parser.parseExpression(expression).getValue(context);
            if (val instanceof UUID uuid) {
                return uuid;
            }
            if (val instanceof Long longVal) {
                return new UUID(0L, longVal);
            }
            if (val instanceof String stringVal) {
                return UUID.fromString(stringVal);
            }
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
        if (expression == null || expression.isEmpty()) {
            return defaultValue;
        }
        try {
            T value = parser.parseExpression(expression).getValue(context, clazz);
            return value != null ? value : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
