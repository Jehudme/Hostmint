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

    String project() default "";

    String entityId() default "";

    String metadata() default "";
}
