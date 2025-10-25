package com.ord.core.word.models.word.enums

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Extra marks or tags for word classification by register or domain")
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