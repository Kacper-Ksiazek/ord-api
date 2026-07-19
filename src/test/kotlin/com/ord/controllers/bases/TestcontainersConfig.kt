package com.ord.controllers.bases

import com.ord.controllers.bases.containers.PostgresTestContainer
import com.ord.testconfig.StubMailTestConfiguration
import com.ord.testconfig.StubOpenAITestConfiguration
import org.flywaydb.core.Flyway
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Testcontainers


@Testcontainers
@SpringBootTest
@Import(StubOpenAITestConfiguration::class, StubMailTestConfiguration::class)
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
            // Configure R2DBC properties for reactive database access
            registry.add("spring.r2dbc.url") {
                container.jdbcUrl.replace("jdbc:postgresql://", "r2dbc:postgresql://")
            }
            registry.add("spring.r2dbc.username", container::getUsername)
            registry.add("spring.r2dbc.password", container::getPassword)

            // Configure Flyway properties explicitly
            registry.add("spring.flyway.url", container::getJdbcUrl)
            registry.add("spring.flyway.user", container::getUsername)
            registry.add("spring.flyway.password", container::getPassword)
            registry.add("spring.flyway.enabled") { "false" } // Disable auto-migration since we do it manually

            // Also configure JDBC datasource for any other components that might need it
            registry.add("spring.datasource.url", container::getJdbcUrl)
            registry.add("spring.datasource.username", container::getUsername)
            registry.add("spring.datasource.password", container::getPassword)
            registry.add("spring.datasource.driver-class-name") { "org.postgresql.Driver" }

            registry.add("elevenlabs.api_key") { "dummy-key" }
            registry.add("elevenlabs.voice_id") { "dummy-voice-id" }

            registry.add("ENV_TEST_PROPERTY") { "1test1" }
            registry.add("jwt.secret_key") {
                "test-jwt-secret-key-with-sufficient-length-for-hs256-algorithm"
            }
            registry.add("jwt.auth_cookie_name") { "AUTH-TOKEN" }
            registry.add("jwt.user_id_claim_name") { "user_id" }
            registry.add("openai.api_key") { System.getenv("OPEN_AI_KEY") ?: "dummy-key" }
            registry.add("openai.api_url") { "https://api.openai.com/v1/responses" }
        }
    }
}

