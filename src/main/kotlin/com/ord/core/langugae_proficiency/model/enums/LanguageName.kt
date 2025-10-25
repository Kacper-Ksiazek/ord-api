package com.ord.core.langugae_proficiency.model.enums

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Supported languages in the platform")
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
