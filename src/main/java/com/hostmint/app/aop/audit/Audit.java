package com.hostmint.app.aop.audit;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Audit {
    String action();

    String entity();

    /**
     * Log level for this audit event. Must be a valid {@link com.hostmint.app.domain.enumeration.LogLevel}
     * constant name: {@code "INFO"}, {@code "WARN"}, {@code "ERROR"}, {@code "DEBUG"}, or {@code "TRACE"}.
     */
    String level() default "INFO";

    String message() default "";

    String project() default "";

    String entityId() default "";

    String metadata() default "";
}
