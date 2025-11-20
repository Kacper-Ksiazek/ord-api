package com.ord.core.word.models.word.enums

import com.ord.shared.annotations.ExportToOpenAPI
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Extra marks or tags for word classification by register or domain")
@ExportToOpenAPI
enum class WordExtraMark {
    OFFENSIVE,
    SLANG,
    FORMAL,
    INFORMAL,
    SCIENTIFIC,
    TECHNICAL,
    LEGAL,
    MEDICAL,
    COLLOQUIAL,
    POETIC
}