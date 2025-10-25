package com.ord.core.word.models.word.enums

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Type of word or expression")
enum class WordType {
    NOUN,
    VERB,
    ADJECTIVE,
    ADVERB,
    IDIOM,
    PHRASE
}