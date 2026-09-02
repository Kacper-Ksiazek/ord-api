package com.ord.core.word.models.word.enums

import com.ord.shared.annotations.ExportToOpenAPI
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Lifecycle status of a vocabulary word")
@ExportToOpenAPI
enum class WordStatus {
    CAPTURED,
    ACTIVE,
}
