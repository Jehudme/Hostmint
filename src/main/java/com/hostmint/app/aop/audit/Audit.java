package com.hostmint.app.aop.audit;

import com.hostmint.app.domain.enumeration.LogLevel;
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Audit {
    String action();

    String entity();

    LogLevel level() default LogLevel.INFO;

    String message() default "";

    // SpEL expressions to capture complex fields
    String project() default ""; // e.g., "#projectDTO"

    String entityId() default ""; // e.g., "#id"

    String metadata() default ""; // e.g., "'Owner: ' + #projectDTO.owner"
}
