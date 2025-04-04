package com.backend.ord.api.responses

import com.backend.ord.domain.persistence.jsons.ExampleSentence
import com.backend.ord.enums.persistence.word.WordExtraMark
import com.backend.ord.enums.persistence.word.WordType

data class GenerateWordManualAIResponse(
    var originalWord: String = "",
    val translation: String,
    val definition: String,
    val type: WordType,
    val extraMark: WordExtraMark? = null,
    var useCases: Set<String> = emptySet(),
    val exampleSentences: Set<ExampleSentence>
)
