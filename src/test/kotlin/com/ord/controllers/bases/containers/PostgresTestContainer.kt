package com.ord.controllers.bases.containers

import org.slf4j.LoggerFactory
import org.testcontainers.containers.PostgreSQLContainer

object PostgresTestContainer {
    private val logger = LoggerFactory.getLogger(PostgresTestContainer::class.java)

    val container: PostgreSQLContainer<*> by lazy {
        PostgreSQLContainer("postgres:16").apply {
            withDatabaseName("test_db")
            withUsername("test_user")
            withPassword("test_pass")

            try {
                logger.info("Starting PostgreSQL test container...")
                start()
                logger.info("PostgreSQL test container started successfully!")
            } catch (e: Exception) {
                logger.error("")
                logger.error("=".repeat(80))
                logger.error("🐳 DOCKER CONNECTION FAILED 🐳")
                logger.error("=".repeat(80))
                logger.error("")
                logger.error("Failed to start PostgreSQL test container!")
                logger.error("")
                logger.error("Common causes:")
                logger.error("  1. ❌ Docker Desktop is not running")
                logger.error("  2. ❌ Docker daemon is not accessible")
                logger.error("  3. ❌ Insufficient permissions to access Docker")
                logger.error("  4. ❌ Docker socket is not available")
                logger.error("")
                logger.error("Solution:")
                logger.error("  ✅ Make sure Docker Desktop is running!")
                logger.error("  ✅ Check: docker ps (should work without errors)")
                logger.error("")
                logger.error("=".repeat(80))
                logger.error("")
                logger.error("Original error: ${e.message}", e)

                throw RuntimeException(
                    """

                    🐳 DOCKER NOT RUNNING! 🐳

                    Failed to start PostgreSQL test container.
                    Please make sure Docker Desktop is running and try again.

                    Run 'docker ps' to verify Docker is accessible.

                    """.trimIndent(),
                    e
                )
            }
        }
    }
}
