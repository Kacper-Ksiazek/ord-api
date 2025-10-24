package com.ord.core

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/health-check")
@Tag(
    name = "6. Utility: Health Check",
    description = "Application health and status monitoring"
)
class HealthCheckController(
    private val databaseClient: DatabaseClient
) {

    enum class HealthStatus {
        UP,
        DOWN
    }

    @GetMapping
    @Operation(
        summary = "Check application health",
        description = "Returns the health status of the application and database connection. No authentication required."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Health status retrieved successfully",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        value = """{"application": "UP", "database": "UP"}"""
                    )]
                )]
            )
        ]
    )
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