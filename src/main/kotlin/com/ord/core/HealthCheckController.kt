package com.ord.core

import com.ord.core.health.AiIntegrationMode
import com.ord.core.health.HealthCheckResponse
import com.ord.core.health.HealthStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.core.env.Environment
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
    private val databaseClient: DatabaseClient,
    private val environment: Environment,
) {

    @GetMapping
    @Operation(
        summary = "Check application health",
        description = "Returns the health status of the application and database connection. " +
            "In the e2e profile, ai and tts are reported as STUB (fixture-based clients, no external API calls). " +
            "No authentication required.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Health status retrieved successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = HealthCheckResponse::class),
                    examples = [
                        ExampleObject(
                            name = "Production",
                            value = """{"application":"UP","database":"UP","ai":"LIVE","tts":"LIVE"}"""
                        ),
                        ExampleObject(
                            name = "E2E profile",
                            value = """{"application":"UP","database":"UP","ai":"STUB","tts":"STUB"}"""
                        ),
                    ]
                )]
            )
        ]
    )
    fun healthCheck(): Mono<ResponseEntity<HealthCheckResponse>> {
        return databaseClient.sql("SELECT 1")
            .fetch()
            .first()
            .map { HealthStatus.UP }
            .onErrorReturn(HealthStatus.DOWN)
            .map { databaseStatus ->
                val integrationMode = resolveIntegrationMode()
                ResponseEntity.ok(
                    HealthCheckResponse(
                        application = HealthStatus.UP,
                        database = databaseStatus,
                        ai = integrationMode,
                        tts = integrationMode,
                    )
                )
            }
    }

    private fun resolveIntegrationMode(): AiIntegrationMode =
        if (environment.activeProfiles.contains("e2e")) {
            AiIntegrationMode.STUB
        } else {
            AiIntegrationMode.LIVE
        }
}
