package com.ord.controllers.bases

import com.ord.controllers.bases.containers.PostgresTestContainer
import org.flywaydb.core.Flyway
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Testcontainers


@Testcontainers
@SpringBootTest
abstract class TestcontainersConfig {

    companion object {
        private val container = PostgresTestContainer.container

        init {
            Flyway.configure()
                .dataSource(container.jdbcUrl, container.username, container.password)
                .load()
                .migrate()
        }

        @JvmStatic
        @DynamicPropertySource
        fun configureDatasource(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", container::getJdbcUrl)
            registry.add("spring.datasource.username", container::getUsername)
            registry.add("spring.datasource.password", container::getPassword)
        }
    }
}

