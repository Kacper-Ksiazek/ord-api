package com.backend.ord.controllers

import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/health-check")
class HealthCheckController(
    private val jdbcTemplate: JdbcTemplate
) {

    enum class HealthStatus {
        UP,
        DOWN
    }

    @GetMapping
    fun healthCheck(): ResponseEntity<Map<String, HealthStatus>> {
        val databaseStatus = run {
            try {
                jdbcTemplate.execute("SELECT 1")

                return@run HealthStatus.UP
            } catch (_: Exception) {
                return@run HealthStatus.DOWN
            }
        }

        return ResponseEntity.ok(
            mapOf(
                "application" to HealthStatus.UP,
                "database" to databaseStatus
            )
        )
    }
}