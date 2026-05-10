package com.hostmint.app.aop.audit;

import com.hostmint.app.service.InternalAuditLogger;
import java.lang.reflect.Method;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {

    private static final Logger LOG = LoggerFactory.getLogger(AuditAspect.class);

    private final InternalAuditLogger auditLogger;
    private final ExpressionParser spelParser;

    public AuditAspect(InternalAuditLogger auditLogger) {
        this.auditLogger = auditLogger;
        this.spelParser = new SpelExpressionParser();
    }

    /**
     * Intercepts methods annotated with @Auditable.
     * @AfterReturning ensures this ONLY fires if the method completes without throwing an exception.
     */
    @AfterReturning(pointcut = "@annotation(auditable)", returning = "result")
    public void logSuccessfulAction(JoinPoint joinPoint, Auditable auditable, Object result) {
        try {
            Long entityId = null;

            // Evaluate SpEL expression to get the entity ID dynamically from method parameters
            if (!auditable.entityIdExpression().isEmpty()) {
                entityId = parseEntityId(joinPoint, auditable.entityIdExpression());
            }

            // Build and save the audit log using your wrapper
            auditLogger.action(auditable.action()).target(auditable.entityName(), entityId).info(auditable.message());
        } catch (Exception e) {
            // Failsafe: Audit logging errors should never crash the application
            LOG.error("Failed to process @Auditable aspect for action {}: {}", auditable.action(), e.getMessage());
        }
    }

    /**
     * Helper method to parse Spring Expression Language (SpEL).
     * This maps method parameter names (like #userId) to their actual runtime values.
     */
    private Long parseEntityId(JoinPoint joinPoint, String expression) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Object[] args = joinPoint.getArgs();
            String[] paramNames = signature.getParameterNames();

            EvaluationContext context = new StandardEvaluationContext();
            for (int i = 0; i < args.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }

            // Parse and return the extracted ID
            Object value = spelParser.parseExpression(expression).getValue(context);
            if (value instanceof Number) {
                return ((Number) value).longValue();
            } else if (value != null) {
                return Long.parseLong(value.toString());
            }
        } catch (Exception e) {
            LOG.warn("Could not evaluate SpEL expression '{}' for auditing: {}", expression, e.getMessage());
        }
        return null;
    }
}
