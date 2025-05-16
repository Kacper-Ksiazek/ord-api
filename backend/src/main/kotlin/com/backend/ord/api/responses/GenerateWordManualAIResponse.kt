package com.backend.ord.api.responses

import com.backend.ord.core.word.model.enums.WordExtraMark
import com.backend.ord.core.word.model.enums.WordType
import com.backend.ord.core.word.model.json.ExampleSentence

data class GenerateWordManualAIResponse(
    var originalWord: String = "",
    val translation: String,
    val definition: String,
    val type: WordType,
    val extraMark: WordExtraMark? = null,
    var useCases: Set<String> = emptySet(),
    val exampleSentences: Set<ExampleSentence>
)
