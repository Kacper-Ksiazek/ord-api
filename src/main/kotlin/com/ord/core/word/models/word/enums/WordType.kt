package com.ord.core.word.models.word.enums

import com.ord.shared.annotations.ExportToOpenAPI
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Type of word or expression")
@ExportToOpenAPI
enum class WordType {
    NOUN,
    VERB,
    ADJECTIVE,
    ADVERB,
    IDIOM,
    PHRASE
}