package com.ord.core.word.api.ai.responses.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "A single vocabulary suggestion with word, translation, and definition")
data class VocabularySuggestion(
    @Schema(
        description = "The suggested word or phrase in the target language",
        example = "reunión"
    )
    val word: String,

    @Schema(
        description = "Translation of the word",
        example = "meeting"
    )
    val translation: String,

    @Schema(
        description = "Brief explanation or context for the word",
        example = "A formal gathering of people for discussion or decision-making, commonly used in professional contexts"
    )
    val definition: String
)

@Schema(description = "List of AI-generated vocabulary suggestions")
data class SuggestVocabularyResponse(
    @Schema(
        description = "List of vocabulary suggestions",
        example = "[{\"word\": \"reunión\", \"translation\": \"meeting\", \"definition\": \"A formal gathering...\"}]"
    )
    val suggestions: List<VocabularySuggestion>
)