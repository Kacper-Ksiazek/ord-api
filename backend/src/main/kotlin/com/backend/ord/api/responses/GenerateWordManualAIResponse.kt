package com.backend.ord.api.responses

import com.backend.ord.domain.embedded.ExampleSentence
import com.backend.ord.enums.word.WordExtraMark
import com.backend.ord.enums.word.WordType

data class GenerateWordManualAIResponse(
    var originalWord: String = "",
    val translation: String,
    val definition: String,
    val type: WordType,
    val extraMark: WordExtraMark? = null,
    var useCases: Set<String> = emptySet(),
    val exampleSentences: Set<ExampleSentence>
)
