package com.hostmint.app;

import com.hostmint.app.config.AsyncSyncConfiguration;
import com.hostmint.app.config.ElasticsearchTestConfiguration;
import com.hostmint.app.config.ElasticsearchTestContainer;
import com.hostmint.app.config.EmbeddedSQL;
import com.hostmint.app.config.JacksonConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = {
        HostMintApp.class,
        JacksonConfiguration.class,
        AsyncSyncConfiguration.class,
        com.hostmint.app.config.JacksonHibernateConfiguration.class,
        ElasticsearchTestConfiguration.class,
    }
)
@EmbeddedSQL
@ImportTestcontainers(ElasticsearchTestContainer.class)
public @interface IntegrationTest {}
