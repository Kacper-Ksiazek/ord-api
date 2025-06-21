package com.ord.controllers.bases.containers

import org.testcontainers.containers.PostgreSQLContainer

object PostgresTestContainer {
    val container: PostgreSQLContainer<*> by lazy {
        PostgreSQLContainer("postgres:16").apply {
            withDatabaseName("test_db")
            withUsername("test_user")
            withPassword("test_pass")
            start()
        }
    }
}
