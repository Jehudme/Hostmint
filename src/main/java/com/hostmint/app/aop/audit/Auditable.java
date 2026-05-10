package com.hostmint.app.aop.audit;

import java.lang.annotation.*;

/**
 * Indicates that a method's successful execution should be recorded in the Audit Log.
 * This will NOT trigger if the method throws an exception.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auditable {
    /**
     * The action being performed (e.g., "PROJECT_CREATED", "USER_DELETED").
     */
    String action();

    /**
     * The name of the entity being manipulated (e.g., "Project", "User").
     */
    String entityName() default "";

    /**
     * A SpEL expression to dynamically extract the entity ID from the method arguments.
     * Example: if method is delete(Long projectId), use entityIdExpression = "#projectId"
     */
    String entityIdExpression() default "";

    /**
     * A human-readable message to describe the action.
     */
    String message() default "Action executed successfully.";
}
