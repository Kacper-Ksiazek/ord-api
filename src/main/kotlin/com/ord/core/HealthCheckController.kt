package com.ord.core

import org.springframework.http.ResponseEntity
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/health-check")
class HealthCheckController(
    private val databaseClient: DatabaseClient
) {

    enum class HealthStatus {
        UP,
        DOWN
    }

    @GetMapping
    fun healthCheck(): Mono<ResponseEntity<Map<String, HealthStatus>>> {
        return databaseClient.sql("SELECT 1")
            .fetch()
            .first()
            .map { HealthStatus.UP }
            .onErrorReturn(HealthStatus.DOWN)
            .map { databaseStatus ->
                ResponseEntity.ok(
                    mapOf(
                        "application" to HealthStatus.UP,
                        "database" to databaseStatus
                    )
                )
            }
    }
}