package com.ord.core.user.model

import com.ord.core.langugae_proficiency.model.enums.LanguageName
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.*

@Schema(description = "User profile information")
data class UserDTO(
    @Schema(description = "Unique user identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    val id: UUID,

    @Schema(description = "User's display name", example = "John Doe")
    var name: String,

    @Schema(description = "User's email address", example = "john.doe@example.com")
    var email: String,

    @Schema(description = "User's native language", example = "ENGLISH", nullable = true)
    var nativeLanguage: LanguageName? = null,

    @Schema(description = "Currently selected language for learning", example = "SPANISH", nullable = true)
    var selectedLearningLanguage: LanguageName? = null,

    @Schema(description = "Whether the user has completed account initialization", example = "true")
    var isAccountInitialized: Boolean = false,

    @Schema(description = "Timestamp when the user account was created", example = "2024-01-15T10:30:00Z")
    val createdAt: Instant = Instant.now(),

    @Schema(description = "Timestamp when the user account was last updated", example = "2024-01-20T15:45:00Z")
    var updatedAt: Instant = Instant.now()
)