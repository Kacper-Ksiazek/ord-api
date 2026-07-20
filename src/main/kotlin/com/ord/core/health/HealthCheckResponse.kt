package com.ord.core.health

import com.ord.shared.annotations.ExportToOpenAPI
import io.swagger.v3.oas.annotations.media.Schema

@ExportToOpenAPI
@Schema(description = "Component health status")
enum class HealthStatus {
    UP,
    DOWN,
}

@ExportToOpenAPI
@Schema(
    description = "External integration mode — `STUB` means fixture-based clients with no outbound HTTP calls",
)
enum class AiIntegrationMode {
    STUB,
    LIVE,
}

@Schema(description = "Application health snapshot")
data class HealthCheckResponse(
    @Schema(example = "UP")
    val application: HealthStatus,

    @Schema(example = "UP")
    val database: HealthStatus,

    @Schema(
        description = "OpenAI client mode",
        example = "LIVE",
    )
    val ai: AiIntegrationMode,

    @Schema(
        description = "ElevenLabs TTS client mode",
        example = "LIVE",
    )
    val tts: AiIntegrationMode,
)
