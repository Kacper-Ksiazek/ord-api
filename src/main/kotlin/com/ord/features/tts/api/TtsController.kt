package com.ord.features.tts.api

import com.ord.config.OpenApiSecurity
import com.ord.core.auth.annotations.AuthenticatedUser
import com.ord.core.user.model.UserDTO
import com.ord.features.tts.api.facades.TtsFacade
import com.ord.features.tts.api.requests.SpeakRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/tts")
@Tag(
    name = "9. TTS",
    description = "Text-to-speech synthesis via ElevenLabs streaming API"
)
@SecurityRequirement(name = OpenApiSecurity.AUTH_COOKIE)
class TtsController(
    private val ttsFacade: TtsFacade,
) {
    @PostMapping("/speak", produces = ["audio/mpeg"])
    @Operation(
        summary = "Synthesize speech from text",
        description = "Stream MP3 audio synthesized from the provided assistant reply text. " +
            "Audio chunks are forwarded to the client as they arrive from ElevenLabs for low-latency playback."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Audio stream started successfully",
                content = [Content(mediaType = "audio/mpeg")]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid request data",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "502",
                description = "ElevenLabs TTS service unavailable",
                content = [Content()]
            ),
        ]
    )
    fun speak(
        @Parameter(hidden = true) @AuthenticatedUser user: UserDTO,
        @Valid @RequestBody body: SpeakRequest,
    ) = ttsFacade.speak(body, user)
}
