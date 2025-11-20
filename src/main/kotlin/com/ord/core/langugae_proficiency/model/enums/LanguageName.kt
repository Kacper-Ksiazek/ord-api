package com.ord.core.langugae_proficiency.model.enums

import com.ord.shared.annotations.ExportToOpenAPI
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Supported languages in the platform")
@ExportToOpenAPI
enum class LanguageName {
    POLISH,
    ENGLISH,
    GERMAN,
    FRENCH,
    SPANISH,
    ITALIAN,
    NORWEGIAN,
    RUSSIAN,
    SLOVENIAN
}
